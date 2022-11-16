#!/bin/bash

DIRNAME=`dirname $0`
cd $DIRNAME/..
SCRIPT_PATH=`pwd`/scripts

echo "task_id,inputdownloading,preprocessing,processing" > $SCRIPT_PATH/archived_overview_data.csv 

for dir in /nfs/archiver/*
do
	inputdownlaoding=0
	preprocessing=0
	processing=0

	for subdir in $dir/*
	do
		#inputdownloading/ preprocessing/ processing/ 
		if [ $subdir == ${dir}/inputdownloading ]; then	
			inputdownlaoding=`sudo ls $subdir | wc -l`
                	#echo $inputdownlaoding
		elif [ $subdir == ${dir}/preprocessing ]; then 
                        preprocessing=`sudo ls $subdir | wc -l`
			#echo $preprocessing
               	elif [ $subdir == ${dir}/processing ]; then
                        processing=`sudo ls $subdir | wc -l`
			#echo $processing
                fi
	done

	task_id=`echo $dir | awk -F'/' '{print $4}'`
	echo $task_id,$inputdownlaoding,$preprocessing,$processing >> $SCRIPT_PATH/archived_overview_data.csv 
done

# Fill and uncoment the following command
# scp -i <key_path> $SCRIPT_PATH/archived_overview_data.csv ubuntu@<dispatcher_ip>:<dispatcher_stats_path>
