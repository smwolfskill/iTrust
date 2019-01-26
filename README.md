# iTrust
iTrust is a medical web system and database that I (wolfski2) modified and extended with seven others on a team as a course project.
It features comprehensive unit and selenium tests with ~90% code coverage.

Files marked with commit message *initialization* were included at the start of the project and were unchanged by our team.

Development progressed entirely according to a schedule and was specified by Use Cases. 
We developed in pairs and practiced extreme programming methodology.
Our development timeline was split into three iterations. 
Every iteration we cycled through three leadership roles of Development Leader, QA Leader, and Planning Leader. 
We split into two subteams of four which in turn split into two pairs.

# My Contribution
In the first two iterations our task was to implement assigned Use Cases
while in the third iteration we created and implemented our own UC's.

##### Files Changed
- (+) indicates I created the file with my partner or by myself.
- (*) indicates I modified the file with my partner or by myself.
## Iteration 1
In Iteration 1 I worked as a pair with Sam on UC41 SendReminders in branch UC41_1. 
We created sendReminders.jsp, SendReminderAction.java,
and the getUpcomingAppts method in ApptDAO.java
to query and return a list of upcoming appointments within a given number of days. 
Standard convention was followed where the jsp determines if input is in correct format, 
and if so calls the action class, which in turn calls the DAO to query the DB. 
A second pair (Jordi and Aidan) worked on the remaining part of UC41 Reminders Outbox. 
We met as a group to review and merge our code into UC41, and only after UC41 was entirely done did 
we merge it into master with the rest of the team. 
##### Files Changed
- [`WebRoot/auth/admin/sendReminders.jsp`](iTrust/WebRoot/auth/admin/sendReminders.jsp) (+)
- [`src/edu/ncsu/csc/itrust/action/SendReminderAction.java`](iTrust/src/edu/ncsu/csc/itrust/action/SendReminderAction.java) (+)
- [`src/edu/ncsu/csc/itrust/dao/mysql/ApptDAO.java`](iTrust/src/edu/ncsu/csc/itrust/dao/mysql/ApptDAO.java) (*)
- [`test/edu/ncsu/csc/itrust/unit/action/SendReminderActionTest.java`](iTrust/test/edu/ncsu/csc/itrust/unit/action/SendReminderActionTest.java) (+)
- [`test/edu/ncsu/csc/itrust/unit/dao/appointment/ApptDAOTest.java`](iTrust/test/edu/ncsu/csc/itrust/unit/dao/appointment/ApptDAOTest.java) (*)
- [`test/edu/ncsu/csc/itrust/selenium/SendReminderTest.java`](iTrust/test/edu/ncsu/csc/itrust/selenium/SendReminderTest.java) (+)

## Iteration 2
In Iteration 2 I worked as a pair with Sean on UC14 Request Biosurveillance. Our pair worked on 
RequestBiosurveillanceAction.java and epidemic detection algorithms using DiagnosesDAO queries,  
while a second pair (Nicholas and Xiaorui) created the UI in WebRoot/auth/hcp/requestBiosurveillance.jsp. 
This time we followed a better procedure by opening a merge request from our branch UC14_1 into UC14. 
Other members of the team posted a review, and we fixed minor issues before merging. This process was 
repeated with a merge request from UC14 into master. I also reviewed and commented on other merge requests.

I fixed the 3rd code smell from Iteration 1, and was also the Quality Assurance Leader for Iteration 2. 
I routinely checked my pair's and any other fully committed iteration 2 code coverage to ensure that our classes had 
at least 80% coverage. I fixed another pair's test that had too low coverage in 
AddPreRegisteredPatientTest.java in 
commit 6e881bda. Before the demo I caught a critical bug in requestBiosurveillance.jsp and added a parameter to main() in 
TestDataGenerator.java to load epidemic data to the DB. If untreated, both problems would have prevented UC14 
from being able to demo.
##### Files Changed
- [`src/edu/ncsu/csc/itrust/action/RequestBiosurveillanceAction.java`](iTrust/src/edu/ncsu/csc/itrust/action/RequestBiosurveillanceAction.java) (+)
- [`WebRoot/auth/hcp/requestBiosurveillance.jsp`](iTrust/WebRoot/auth/hcp/requestBiosurveillance.jsp) (*)
- [`test/edu/ncsu/csc/itrust/unit/action/RequestBiosurveillanceActionTest.java`](iTrust/test/edu/ncsu/csc/itrust/unit/action/RequestBiosurveillanceActionTest.java) (+)
- [`test/edu/ncsu/csc/itrust/unit/dao/patient/AddPreRegisteredPatientTest.java`](iTrust/test/edu/ncsu/csc/itrust/unit/dao/patient/AddPreRegisteredPatientTest.java) (*)
- [`test/edu/ncsu/csc/itrust/unit/datagenerators/TestDataGenerator.java`](iTrust/test/edu/ncsu/csc/itrust/unit/datagenerators/TestDataGenerator.java) (*)

## Iteration 3
In Iteration 3 I worked as a pair on UCOwn_2 Heatmap in branch T4_Own_2. We created 
viewWeeklySchedule.jsp
and WeeklyScheduleAction.java for displaying a heatmap of number of appointments in a given week vs. hour of day,
with a darker shade of red indicating more appointments in a given hour in a day. We couldn't find a satisfactory
heatmap template on google charts or anything similar, so we created our own using a jsp table and an algorithm that
finds all appointments in a given week, sorts them by day and by hour in the day into a 2D array, and then maps that array into
a 2D heatmap color array to be used in the jsp table. We made a merge request from T4_Own_2 directly into master, because
we essentially split UCOwn into two completely independent use cases. It was merged after review and minor fixing.
Again I reviewed and commented on other merge requests with my partner.

Although I was no longer Quality Assurance Leader in Iteration 3 I still paid close attention to test results of the entire
iTrust project, and fixed a UC92 bug in AddPreRegisteredPatientTest.java and TestDataGenerator.java that was preventing
other independent tests from passing due to the accidental insertion of one too many patients into the DB.
##### Files Changed
- [`WebRoot/auth/admin/viewWeeklySchedule.jsp`](iTrust/WebRoot/auth/admin/viewWeeklySchedule.jsp) (+)
- [`src/edu/ncsu/csc/itrust/action/WeeklyScheduleAction.java`](iTrust/src/edu/ncsu/csc/itrust/action/WeeklyScheduleAction.java) (+)
- [`test/edu/ncsu/csc/itrust/unit/action/WeeklyScheduleActionTest.java`](iTrust/test/edu/ncsu/csc/itrust/unit/action/WeeklyScheduleActionTest.java) (+)
- [`test/edu/ncsu/csc/itrust/unit/dao/patient/AddPreRegisteredPatientTest.java`](iTrust/test/edu/ncsu/csc/itrust/unit/dao/patient/AddPreRegisteredPatientTest.java) (*)
- [`test/edu/ncsu/csc/itrust/unit/datagenerators/TestDataGenerator.java`](iTrust/test/edu/ncsu/csc/itrust/unit/datagenerators/TestDataGenerator.java) (*)
- [`test/edu/ncsu/csc/itrust/selenium/viewWeeklyScheduleTest.java`](iTrust/test/edu/ncsu/csc/itrust/selenium/viewWeeklyScheduleTest.java) (+)
