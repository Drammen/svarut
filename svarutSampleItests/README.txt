Successful test execution has the following prerequisites :

* Mule server must be running with all services listed in serverconfig/<target-environment>/mule/services_to_start.
* OpenMq broker must be running in the target environment.
* SkjemaWar must be up and running on an application server (Tomcat).
* An smtp server must be available as configured in serverconfig/<target-environment>/tomcat/lib/skjema.properties.

Select service environment to exercise by setting the environment variable TargetServer either LOCALHOST (default), ACCEPTANCE or INTEGRATION.
I.E. export TargetServer=INTEGRATION   