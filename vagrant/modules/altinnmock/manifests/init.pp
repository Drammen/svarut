class altinnmock {

  package { "openjdk-6-jre-headless":
    ensure => latest,
  }

  file { "/opt/altinnMock":
    ensure => "directory",
    mode  => 777,
  }
  
  file { "/opt/altinnMock/altinnMockService.jar":
    ensure => present,
	group => 'root',
	owner => 'root',
	source => ['puppet:///modules/altinnmock/altinnMockService.jar'],
    mode  => 755,
  }
  
  file { "/etc/init.d/altinnMockService":
    ensure => present,
	group => 'root',
	owner => 'root',
	source => ['puppet:///modules/altinnmock/altinnMockService'],
    mode  => 755,
  }
  
  service { "altinnMockService":
    ensure => running,
	require => [Package["openjdk-6-jre-headless"], File["/etc/init.d/altinnMockService"],File["/opt/altinnMock/altinnMockService.jar"]],
  }
}

