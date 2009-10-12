These tests verify that the various protocols and the EL4J extensions work as desired. 

Remark about the loadbalancing test: it launches several java processes and lets them run for a constant period of time (about 3-4 minutes).
This can cause issues when you run the tests in quick sequence (as the old tests may still be running). 
