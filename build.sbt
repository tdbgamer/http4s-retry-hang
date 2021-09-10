ThisBuild / organization := "com.example"
ThisBuild / scalaVersion := "2.13.5"

lazy val root = (project in file(".")).settings(
  name := "http4s-retry",
  libraryDependencies ++= Seq(
    // "core" module - IO, IOApp, schedulers
    // This pulls in the kernel and std modules automatically.
    "org.typelevel" %% "cats-effect" % "3.2.8",
    // concurrency abstractions and primitives (Concurrent, Sync, Async etc.)
    "org.typelevel" %% "cats-effect-kernel" % "3.2.8",
    // standard "effect" library (Queues, Console, Random etc.)
    "org.typelevel" %% "cats-effect-std" % "3.2.8",
    // better monadic for compiler plugin as suggested by documentation
    "org.http4s" %% "http4s-blaze-server" % "1.0.0-M25",
    "org.http4s" %% "http4s-blaze-client" % "1.0.0-M25",
    "org.http4s" %% "http4s-core" % "1.0.0-M25",
    compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
  )
)
