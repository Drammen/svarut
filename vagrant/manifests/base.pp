class lucid64 {
 group { "puppet" :
   ensure => present
 }
}
class apt {
  exec { "apt-update":
    command => "/usr/bin/apt-get update"
  }

  # Ensure apt-get update has been run before installing any packages
  Exec["apt-update"] -> Package <| |>
}

include apt
include lucid64
include mail
include ldap
include altinnmock
