class ldap  {

  file { "/var/lib/ldap":
    ensure => directory,
	group => 'openldap',
	owner => 'openldap',
	require => Package["slapd"],
    mode => 750,
  }

  file { "/etc/ldap/slapd.d":
    ensure => directory, # so make this a directory
    recurse => true, # enable recursive directory management
    purge => true, # purge all unmanaged junk
    force => true, # also purge subdirs and links etc.
    owner => "openldap",
    group => "openldap",
    mode => 0640, # this mode will also apply to files from the source directory
	require => Package["slapd"],
    source => ['puppet:///modules/ldap/etc/ldap/slapd.d'],
  }

  file { "/etc/ldap/people.ldif":
    ensure => present,
	require => [Package["slapd"]],
	group => 'openldap',
	owner => 'openldap',
    mode => 750,
    source => ['puppet:///modules/ldap/etc/ldap/people.ldif'],
  }

  package { "ldap-utils":
    ensure => latest,
  }

  package { "slapd":
    ensure => latest,
  }

  service { "slapd":
    ensure => running,
	require => [Package["slapd"]]
  }

  exec { "add ldap users":
    command => "/bin/sleep 5 && /usr/bin/sudo /usr/bin/ldapadd -x -D cn=admin,dc=home,dc=local -w admin -f /etc/ldap/people.ldif",
	returns => [0,68],
	require => [Package["ldap-utils"], Exec ["restart slapd"]]
  }


  exec { "restart slapd":
    command => "/usr/bin/sudo /etc/init.d/slapd restart",
	require => [File ["/etc/ldap/people.ldif"], File["/etc/ldap/slapd.d"], Package["slapd"]],
  }

}

