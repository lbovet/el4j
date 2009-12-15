Example project showing how to store a maven 2 repository in a Windows share (smb)

How to upload an artifact into the smb-m2repo:
  - ensure that the shared folder is accessible (see <distributionManagement> section in pom.xml)
  - copy your artifact (jar, pom) into folder m2upload according to the guidelines
    specified in http://el4j.sourceforge.net/plugins/maven-repohelper-plugin/index.html
    (here the base folder is m2upload, not libraries).
  - run 'mvn repohelper:deploy-libraries', which copies your artifacts to the
    m2repository folder using the right structure
  - delete the files in m2upload or add them to version control, too.
