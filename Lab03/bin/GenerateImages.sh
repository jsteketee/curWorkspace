#!/bin/bash



for((i = 0; i < 4 ; i++))
do 
	for ((a = 3; a < 9; a++))
	do   
		echo $a
		java ClusterImage headshot.jpg $a $i
		java ClusterImage tiger.jpg $a $i
		java ClusterImage grapefruit.jpg $a $i
	done
done 

