class lucid64 {
 group { "puppet" :
   ensure => present
 }
}
class apt {
  file { "/etc/apt/apt.conf.d/30proxy" :
  	ensure => present,
	content => 'Acquire::http { Proxy "http://85.19.187.23:8080"; };
Acquire::https { Proxy "http://85.19.187.23:8080"; };'
  }

  exec { "apt-update":
    command => "/usr/bin/apt-get update",
	require => File["/etc/apt/apt.conf.d/30proxy"]
  }

  # Ensure apt-get update has been run before installing any packages
  Exec["apt-update"] -> Package <| |>
}

include apt
include lucid64
include mail
include ldap
include altinnmock
