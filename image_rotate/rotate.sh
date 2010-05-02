#!/bin/bash

rm -rf out
mkdir out

pushd src
	for i in $( ls ); do
		convert $i -resize 533x400\! ../out/`echo ${i}|sed "s|\.png$||"`_p.png
		convert $i -gravity NorthWest -crop 800x480+0+0 -resize 400x240 ../out/`echo ${i}|sed "s|\.png$||"`_l.png
	done
	convert img00053.png -crop 600x600+100+0 -resize 72x72 ../out/icon_hdpi.png
	convert img00053.png -crop 600x600+100+0 -resize 48x48 ../out/icon_mdpi.png
	convert img00053.png -crop 600x600+100+0 -resize 36x36 ../out/icon_ldpi.png
popd

