package com.github

import cats.effect.IOApp
import cats.effect.IO
import cats.implicits._
import org.http4s.blaze.client.BlazeClientBuilder
import scala.concurrent.ExecutionContext
import org.http4s.client.middleware.Retry
import org.http4s.client.middleware.RetryPolicy
import scala.concurrent.duration._
import org.http4s.blaze.server.BlazeServerBuilder
import org.http4s.HttpApp
import org.http4s.Request
import org.http4s.Response
import cats.data.Kleisli
import org.http4s.Status
import org.http4s.Method
import org.http4s.implicits._
import fs2.Stream

object Main extends IOApp.Simple {
  val clientRes = BlazeClientBuilder[IO](ExecutionContext.global).resource
  val retriable: (Request[IO], Either[Throwable, Response[IO]]) => Boolean = {
    case (_, Right(resp)) if resp.status == Status.BadRequest => false
    case _                                                    => true
  }
  val policy =
    RetryPolicy[IO](RetryPolicy.exponentialBackoff(1.second, 3), retriable)
  val retryClient = clientRes.map(Retry(policy)(_))

  val app: HttpApp[IO] = Kleisli.pure(Response[IO](Status.NotFound))

  val server = BlazeServerBuilder[IO](ExecutionContext.global)
    .bindLocal(8888)
    .withHttpApp(app)
    .serve
    .compile
    .drain

  val req = Request[IO](Method.POST, uri"http://localhost:8888")

  val reqs = Stream[IO, Request[IO]](List.fill(50)(req): _*)

  def run: IO[Unit] = (server.background product retryClient).use {
    case (_, client) =>
      reqs.parEvalMap(25)(client.expect[Unit](_)).compile.drain
  }

}
