java OptimalTSP test/24-10000 > output/0-1-24  &
java OptimalTSP test/24-10000 > output/0-2-24  &
java OptimalTSP test/24-10000 > output/0-3-24  &

java -Dpj.nt=1 OptimalTSPSMP test/24-10000 > output/1-1-24  &
java -Dpj.nt=1 OptimalTSPSMP test/24-10000 > output/1-2-24  &
java -Dpj.nt=1 OptimalTSPSMP test/24-10000 > output/1-3-24  &

java -Dpj.nt=2 OptimalTSPSMP test/24-10000 > output/2-1-24  &
java -Dpj.nt=2 OptimalTSPSMP test/24-10000 > output/2-2-24  &
java -Dpj.nt=2 OptimalTSPSMP test/24-10000 > output/2-3-24  &

java -Dpj.nt=3 OptimalTSPSMP test/24-10000 > output/3-1-24  &
java -Dpj.nt=3 OptimalTSPSMP test/24-10000 > output/3-2-24  &
java -Dpj.nt=3 OptimalTSPSMP test/24-10000 > output/3-3-24  &

java -Dpj.nt=4 OptimalTSPSMP test/24-10000 > output/4-1-24  &
java -Dpj.nt=4 OptimalTSPSMP test/24-10000 > output/4-2-24  &
java -Dpj.nt=4 OptimalTSPSMP test/24-10000 > output/4-3-24  &

java -Dpj.nt=8 OptimalTSPSMP test/24-10000 > output/8-1-24  &
java -Dpj.nt=8 OptimalTSPSMP test/24-10000 > output/8-2-24  &
java -Dpj.nt=8 OptimalTSPSMP test/24-10000 > output/8-3-24  &

