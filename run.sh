#!/bin/bash

cd classes

for i in {1..6}
do
	java Fun_with_Robots ../inputs/input${i}.txt ../outputs/out${i}.txt
done