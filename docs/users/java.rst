User Guide - Java
=================

Starting to use Hangar as your dependency management system is simple. 

To Download
-----------

To use Hangar as your main repository, add the following directive to your *settings.xml*

.. code:: xml

  <mirrors>
    <mirror>
      <id>hangar</id>
      <url><your-hangar-url>/java</url>
      <mirrorOf>central</mirrorOf>
    </mirror>
  </mirrors>
  
where *<your-hangar-url>* is the URL given to you by your friendly system administrator for where it's running. Simples!

To Upload
---------

Obviously, you've written something incredible and you want to push your new artifact back into Hangar. Add the following directive into your *pom.xml*

.. code:: xml

	<distributionManagement>
	   <snapshotRepository>
	      <id>hangar-snapshots</id>
	      <url>http://<your-hangar-url>/java/snapshots</url>
	   </snapshotRepository>
	   <repository>
	      <id>hangar-releases</id>
	      <url>http://<your-hangar-url>/java/releases</url>
	   </repository>
	</distributionManagement>
	
where *<your-hangar-url>* is the URL given to you by your friendly system administrator for where it's running. Simples!