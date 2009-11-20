---------------------------------------------------------------------------------------------

SSL certificate generation helper (SSL convenience scripts)

---------------------------------------------------------------------------------------------

   This directory contains three scripts for the creation of ssl certificates (for CA, client and server)
   using OpenSSL. 

   The configuration for all three certificate types is situated inside the openssl.conf file. 
   All settings that have to be assigned are marked like [DESCRIPTION OF SETTING].

   Just replace these […] with your desired value.


   For example replace

       unique_subject  = [NAME OF THE CA]

   with

       unique_subject  = My own Certification Authority


	   After this has been done, just execute the shell script to start the corresponding certificate generation.
