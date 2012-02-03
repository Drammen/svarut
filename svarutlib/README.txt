==== HVORDAN FINNE FRAM I KODEN

Koden er organisert rundt fire sentrale klasser: Forsendelse, ServiceContext, ServiceDelegate og Dispatcher.

* Forsendelse ineholder dokumentet som skal sendes i form av en pdf adresseinformasjon og ønsket ShipmentPolicy (NorgeDotNoOgApost, KunApost ...). 
  Dao-klassen ForsendelsesArkiv står for lagring og gjenfinning av Forsendelser.

* Dispatcher (interface) - Det finnes mange dispatchers, og DispatcherFactory setter opp og leverer en som er egnet til gitt ShipmentPolicy for en Forsendelse. 
  En dispatcher har som oppgave å utføre forsendelsen i henhold til gitt ShipmentPolicy.     

* ServiceContext holder all context for alle dispatchers og all øvrig kode, og konfigureres typisk fra Spring.
  Her holdes også et sett med integrasjons klasser som typisk har navn som slutter med 'Facade'.
  Disse klassene  kjenner til hvordan kommunisere med Altin, print-provider, ldap, smtp etc.

* ServiceDelegate er felles entry-point for interaksjon med SvarUt.
  Det vil si at alle service-grensesnitt (soap, rest, jms) delegerer metodene sine til denne.
  ServiceDelegateImpl kjenner til ServiceContext og DispatcherFactory.

SvarUt benytter seg av en rekke jobber (se Job) som eksekverer i bakgrunnen. Disse kontrolleres av JobController.
Jmx grensesnittet er bygget rundt ServiceDelegate, og benyttes til fortløpende å holde oppsyn med SvarUt sin helsetilstand.

