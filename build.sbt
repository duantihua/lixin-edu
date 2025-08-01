import org.openurp.parent.Dependencies.*
import org.openurp.parent.Settings.*

ThisBuild / version := "0.0.2-SNAPSHOT"

ThisBuild / scmInfo := Some(
  ScmInfo(
    url("https://github.com/urp-school/lixin-edu-core"),
    "scm:git@github.com:urp-school/lixin-edu-core.git"
  )
)

ThisBuild / developers := List(
  Developer(
    id = "chaostone",
    name = "Tihua Duan",
    email = "duantihua@gmail.com",
    url = url("http://github.com/duantihua")
  )
)

ThisBuild / description := "LIXIN Edu Core Library"
ThisBuild / homepage := Some(url("http://openurp.github.io/lixin-edu-core/index.html"))

val apiVer = "0.44.1"
val starterVer = "0.3.59"
val eduCoreVer = "0.3.13"
val openurp_edu_api = "org.openurp.edu" % "openurp-edu-api" % apiVer
val openurp_std_api = "org.openurp.std" % "openurp-std-api" % apiVer
val openurp_stater_ws = "org.openurp.starter" % "openurp-starter-ws" % starterVer
val openurp_edu_core = "org.openurp.edu" % "openurp-edu-core" % eduCoreVer

lazy val root = (project in file("."))
  .settings(
    name := "lixin-edu-core",
    organization := "net.openurp.lixin",
    common,
    libraryDependencies ++= Seq(openurp_edu_api, openurp_std_api, openurp_edu_core),
    libraryDependencies ++= Seq(beangle_ems_app),
    libraryDependencies ++= Seq(beangle_doc_transfer, beangle_cdi, beangle_security)
  )
