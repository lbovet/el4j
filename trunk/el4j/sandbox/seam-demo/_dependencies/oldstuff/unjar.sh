cd out
echo $1
jar xvf $1 >../logs/`basename $1`.txt 2>../logs/`basename $1`.err.txt
rm -fv LICENSE
cd ..
