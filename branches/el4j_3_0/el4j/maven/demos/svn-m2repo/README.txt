Example project showing how to store a maven 2 repository in svn/cvs etc.

How to upload an artifact into the svn-m2repo:
  - copy your artifact (jar, pom) into folder m2upload according to the guidelines
    specified in http://el4j.sourceforge.net/plugins/maven-repohelper-plugin/index.html
    (here the base folder is m2upload, not libraries).
  - run 'mvn repohelper:deploy-libraries', which copies your artifacts to the
    m2repository folder using the right structure
  - add all files in m2repository to version control system and commit
  - delete the files in m2upload or add them to version control, too.
