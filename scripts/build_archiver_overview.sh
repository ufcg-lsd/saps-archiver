#!/bin/bash

DIRNAME=`dirname $0`
cd $DIRNAME/.

echo "task_id,inputdownloading,preprocessing,processing" > ./archived_overview_data.csv 

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
	echo $task_id,$inputdownlaoding,$preprocessing,$processing >> ./archived_overview_data.csv 
done

# Update these fields 
ssh_key_file="/home/ubuntu/.ssh/saps22"
remote_user="ubuntu"
dispatcher_ip="127.0.0.1"
dispatcher_stats_path="/home/ubuntu/saps-dispatcher/stats/archived_overview_data.csv"

scp -i $ssh_key_file ./archived_overview_data.csv $remote_user@$dispatcher_ip:$dispatcher_stats_path
