The data files in this directory were obtained from the public IANA time zone database,
http://www.iana.org/time-zones, version 2014e.

Run the following command (from tzdata dir) to replace tabs with spaces in tzdata files for better readability in Android Studio
find . ! -type d -exec bash -c 'expand -t 8 "$0" > /tmp/e && mv /tmp/e "$0"' {} \;
