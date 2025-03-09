# salary-analyzer
Salary Analyzer
BIG COMPANY is employing a lot of employees. Company would like to analyze its organizational 
structure and identify potential improvements. Board wants to make sure that every manager earns at least 20% more than 
the average salary of its direct subordinates, but no more than 50% more than that average. 
Company wants to avoid too long reporting lines, therefore we would like to identify all employees which have 
more than 4 managers between them and the CEO.


Run Instructions

Run the [SalaryAnalyzerApplication.java](src%2Fmain%2Fjava%2Fcom%2Fswissre%2Fsalaryanalyzer%2FSalaryAnalyzerApplication.java)

use the below curl to access the end point

curl --location 'http://localhost:8080/analyze' \
--form 'file=@"/C:/Users/bhara/OneDrive/Desktop/employees.csv"'

For csv file check the reference
[employees.csv](src%2Ftest%2Fresources%2Femployees.csv)