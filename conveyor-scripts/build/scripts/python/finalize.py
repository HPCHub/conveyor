#
# This script append to every jsub pipeline as last job.
# It is at least update jsub$runs table to notify pipeline is end.
# But with time it can accrete some more functionality.
#


import cx_Oracle


runId = int("${project.run.id}");


connection = cx_Oracle.connect("msdba/msdba@10.210.31.7:1521/biodb")
cursor = connection.cursor()

cursor.execute(
    "UPDATE jsub$runs SET stop_date = CURRENT_TIMESTAMP WHERE run_id = :run_id",
     { ":run_id" : runId }
)

connection.commit()
cursor.close()