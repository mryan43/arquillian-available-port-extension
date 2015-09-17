Arquillian available port extension
===================================
This arquillian extension will look for an available network port and expose the number as a system property
called "available.port" that you can then use in your arquillian.xml file to configure a container.

The system property is only present while the configuration is parsed, and any previous value is restored after that.

User Guide
----------
Simply add the extension jar to your test classpath and use ${available.port} in your arquillian.xml


Why use this ?
--------------

This makes your arquillian tests execution more robust in dev and CI environments.

- Container agnostic
While some container adapters already support this feature through special port values such as "0" or "-1", it is not the case for all
container adapters such as the tomcat for example.

- Test framework agnostic
While some other solutions such as custom Junit runners, Maven plugins (build-helper) can provide a similar feature,
this one works "out of the box" all the time, unaffected by whether your are run with Junit, TestNG, Maven, Intellij, Eclipse, etc...