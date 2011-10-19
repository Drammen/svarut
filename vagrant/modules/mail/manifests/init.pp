class mail {
  package { "dovecot-postfix":
    ensure => latest,
  }
  
  file { "/etc/postfix/main.cf":
    ensure => present,
	group => 'root',
	owner => 'root',
	source => ['puppet:///modules/mail/etc/postfix/main.cf'],
	require => Package["dovecot-postfix"]
  }
  
  file { "/etc/postfix/virtual":
    ensure => present,
	group => 'root',
	owner => 'root',
	source => ['puppet:///modules/mail/etc/postfix/virtual'],
	require => Package["dovecot-postfix"]
  }
  
  exec { "virtual.db":
    command =>  "/usr/sbin/postmap /etc/postfix/virtual",
	creates => "/etc/postfix/virtual.db",
	require => [File["/etc/postfix/virtual"],File["/etc/postfix/main.cf"]],
	before => Exec["postfix reload"]
  }

  exec { "postfix reload":
    command => "/usr/sbin/postfix reload",
  } 

  service { "dovecot":
    ensure => running,
	require => [Package["dovecot-postfix"]]
  }

  service { "postfix":
    ensure => running,
	require => [Package["dovecot-postfix"],File["/etc/postfix/main.cf"],File["/etc/postfix/virtual"]]
  }
}

