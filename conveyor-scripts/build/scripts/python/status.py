#
# This script calls every time when starts next pipeline job.
#


import argparse
import cx_Oracle


parser = argparse.ArgumentParser()

parser.add_argument("--status", metavar="status", action="store", nargs=1, type=str, help="pipeline status", required=True, dest="status")
parser.add_argument("--run-id", metavar="run-id", action="store", nargs=1, type=int, help="pipeline run id", required=True, dest="runId")

args = parser.parse_args()


status = args.status[0].strip()
runId  =  args.runId[0]


if len(status) < 1:
    exit("Status length must be a non empty string")


connection = cx_Oracle.connect("msdba/msdba@10.210.31.7:1521/biodb")
cursor = connection.cursor()

cursor.execute(
    "UPDATE jsub$runs SET current_job = :current_job WHERE run_id = :run_id",
     { ":current_job" : status, ":run_id" : runId }
)

connection.commit()
cursor.close()