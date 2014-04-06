package net.danlew.android.joda;

import android.content.Context;
import org.joda.time.DateTime;
import org.joda.time.DateTimeFieldType;
import org.joda.time.DateTimeZone;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Interval;
import org.joda.time.LocalDate;
import org.joda.time.Minutes;
import org.joda.time.ReadableDuration;
import org.joda.time.ReadableInstant;
import org.joda.time.ReadablePartial;
import org.joda.time.Seconds;
import org.joda.time.Weeks;
import org.joda.time.Years;

/**
 * A replacement for android.text.format.DateUtils that uses Joda-Time classes.
 *
 * It is not a 1:1 implementation of Android's DateUtils.  There are a few improvements made:
 *
 * - Deprecated constants have been removed.
 *
 * - Constants which are better represented by Joda-Time classes have been removed.
 *
 * - minResolution has been removed from relative time span methods because they make no sense.
 * All it does is remove meaningful information from the string.  E.g., it turns
 * "in 30 seconds" into "in 0 minutes", or "in 5 hours" into "in 0 days".  Having 0 of anything
 * doesn't tell the user anything and should not be encouraged.
 */
public class DateUtils {

    // The following FORMAT_* symbols are used for specifying the format of
    // dates and times in the formatDateRange method.
    public static final int FORMAT_SHOW_TIME = android.text.format.DateUtils.FORMAT_SHOW_TIME;
    public static final int FORMAT_SHOW_WEEKDAY = android.text.format.DateUtils.FORMAT_SHOW_WEEKDAY;
    public static final int FORMAT_SHOW_YEAR = android.text.format.DateUtils.FORMAT_SHOW_YEAR;
    public static final int FORMAT_NO_YEAR = android.text.format.DateUtils.FORMAT_NO_YEAR;
    public static final int FORMAT_SHOW_DATE = android.text.format.DateUtils.FORMAT_SHOW_DATE;
    public static final int FORMAT_NO_MONTH_DAY = android.text.format.DateUtils.FORMAT_NO_MONTH_DAY;
    public static final int FORMAT_NO_NOON = android.text.format.DateUtils.FORMAT_NO_NOON;
    public static final int FORMAT_NO_MIDNIGHT = android.text.format.DateUtils.FORMAT_NO_MIDNIGHT;
    public static final int FORMAT_ABBREV_TIME = android.text.format.DateUtils.FORMAT_ABBREV_TIME;
    public static final int FORMAT_ABBREV_WEEKDAY = android.text.format.DateUtils.FORMAT_ABBREV_WEEKDAY;
    public static final int FORMAT_ABBREV_MONTH = android.text.format.DateUtils.FORMAT_ABBREV_MONTH;
    public static final int FORMAT_NUMERIC_DATE = android.text.format.DateUtils.FORMAT_NUMERIC_DATE;
    public static final int FORMAT_ABBREV_RELATIVE = android.text.format.DateUtils.FORMAT_ABBREV_RELATIVE;
    public static final int FORMAT_ABBREV_ALL = android.text.format.DateUtils.FORMAT_ABBREV_ALL;

    /**
     * We don't want consumers of DateUtils to use this, but we do need it internally to calibrate
     * times to UTC for formatting purposes.
     */
    private static final int FORMAT_UTC = android.text.format.DateUtils.FORMAT_UTC;

    private static final DateTime EPOCH = new DateTime(0, DateTimeZone.UTC);

    /**
     * Formats a date or a time according to the local conventions.
     *
     * Since ReadablePartials don't support all fields, we fill in any blanks
     * needed for formatting by using the epoch (1970-01-01T00:00:00Z).
     *
     * See {@link android.text.format.DateUtils#formatDateTime} for full docs.
     *
     * @param context the context is required only if the time is shown
     * @param time a point in time
     * @param flags a bit mask of formatting options
     * @return a string containing the formatted date/time.
     */
    public static String formatDateTime(Context context, ReadablePartial time, int flags) {
        return android.text.format.DateUtils.formatDateTime(context, toMillis(time), flags | FORMAT_UTC);
    }

    /**
     * Formats a date or a time according to the local conventions.
     *
     * See {@link android.text.format.DateUtils#formatDateTime} for full docs.
     *
     * @param context the context is required only if the time is shown
     * @param time a point in time
     * @param flags a bit mask of formatting options
     * @return a string containing the formatted date/time.
     */
    public static String formatDateTime(Context context, ReadableInstant time, int flags) {
        return android.text.format.DateUtils.formatDateTime(context, toMillis(time), flags | FORMAT_UTC);
    }

    /**
     * Formats a date or a time range according to the local conventions.
     *
     * You should ensure that start/end are in the same timezone; formatDateRange()
     * doesn't handle start/end in different timezones well.
     *
     * See {@link android.text.format.DateUtils#formatDateRange} for full docs.
     *
     * @param context the context is required only if the time is shown
     * @param start the start time
     * @param end the end time
     * @param flags a bit mask of options
     * @return a string containing the formatted date/time range
     */
    public static String formatDateRange(Context context, ReadablePartial start, ReadablePartial end, int flags) {
        return formatDateRange(context, toMillis(start), toMillis(end), flags);
    }

    /**
     * Formats a date or a time range according to the local conventions.
     *
     * You should ensure that start/end are in the same timezone; formatDateRange()
     * doesn't handle start/end in different timezones well.
     *
     * See {@link android.text.format.DateUtils#formatDateRange} for full docs.
     *
     * @param context the context is required only if the time is shown
     * @param start the start time
     * @param end the end time
     * @param flags a bit mask of options
     * @return a string containing the formatted date/time range
     */
    public static String formatDateRange(Context context, ReadableInstant start, ReadableInstant end, int flags) {
        return formatDateRange(context, toMillis(start), toMillis(end), flags);
    }

    private static String formatDateRange(Context context, long startMillis, long endMillis, int flags) {
        // Buffer is needed, otherwise end time is off by 1 crucial second; however, don't do this
        // if they are already equal (that indicates a point in time rather than a range).
        if (startMillis != endMillis) {
            endMillis += 1000;
        }

        return android.text.format.DateUtils.formatDateRange(context, startMillis, endMillis, flags | FORMAT_UTC);
    }

    private static long toMillis(ReadablePartial time) {
        return time.toDateTime(EPOCH).getMillis();
    }

    private static long toMillis(ReadableInstant time) {
        DateTime dateTime = time instanceof DateTime ? (DateTime) time : new DateTime(time);
        DateTime utcDateTime = dateTime.withZoneRetainFields(DateTimeZone.UTC);
        return utcDateTime.getMillis();
    }

    /**
     * Formats an elapsed time in the form "MM:SS" or "H:MM:SS"
     * for display on the call-in-progress screen.
     *
     * See {@link android.text.format.DateUtils#formatElapsedTime} for full docs.
     *
     * @param elapsedDuration the elapsed duration
     */
    public static String formatElapsedTime(ReadableDuration elapsedDuration) {
        return formatElapsedTime(null, elapsedDuration);
    }

    /**
     * Formats an elapsed time in a format like "MM:SS" or "H:MM:SS" (using a form
     * suited to the current locale), similar to that used on the call-in-progress
     * screen.
     *
     * See {@link android.text.format.DateUtils#formatElapsedTime} for full docs.
     *
     * @param recycle {@link StringBuilder} to recycle, or null to use a temporary one.
     * @param elapsedDuration the elapsed duration
     */
    public static String formatElapsedTime(StringBuilder recycle, ReadableDuration elapsedDuration) {
        return android.text.format.DateUtils.formatElapsedTime(recycle,
            elapsedDuration.toDuration().toStandardSeconds().getSeconds());
    }

    /**
     * See {@link android.text.format.DateUtils#isToday} for full docs.
     *
     * @return true if the supplied when is today else false
     */
    public static boolean isToday(ReadablePartial time) {
        if (!time.isSupported(DateTimeFieldType.dayOfMonth())
            || !time.isSupported(DateTimeFieldType.monthOfYear())
            || !time.isSupported(DateTimeFieldType.year())) {
            throw new IllegalArgumentException("isToday() must be passed a ReadablePartial that supports day of " +
                "month, month of year and year.");
        }

        LocalDate localDate = time instanceof LocalDate ? (LocalDate) time : new LocalDate(time);
        return LocalDate.now().compareTo(localDate) == 0;
    }

    /**
     * See {@link android.text.format.DateUtils#isToday} for full docs.
     *
     * @return true if the supplied when is today else false
     */
    public static boolean isToday(ReadableInstant time) {
        return LocalDate.now().compareTo(new LocalDate(time)) == 0;
    }

    /**
     * Returns a string describing 'time' as a time relative to the current time.
     *
     * Missing fields from 'time' are filled in with values from the current time.
     *
     * @see #getRelativeTimeSpanString(Context, ReadableInstant, ReadableInstant, int)
     */
    public static CharSequence getRelativeTimeSpanString(Context context, ReadablePartial time) {
        return getRelativeTimeSpanString(context, time, LocalDate.now());
    }

    /**
     * Returns a string describing 'time' as a time relative to the current time.
     *
     * @see #getRelativeTimeSpanString(Context, ReadableInstant, ReadableInstant, int)
     */
    public static CharSequence getRelativeTimeSpanString(Context context, ReadableInstant time) {
        return getRelativeTimeSpanString(context, time, DateTime.now());
    }

    /**
     * Returns a string describing 'time' as a time relative to 'now'.
     *
     * Missing fields from 'time' and 'now' are filled in with values from the current time.
     *
     * @see #getRelativeTimeSpanString(Context, ReadableInstant, ReadableInstant, int)
     */
    public static CharSequence getRelativeTimeSpanString(Context context, ReadablePartial time, ReadablePartial now) {
        DateTime dtNow = DateTime.now();
        return getRelativeTimeSpanString(context, time.toDateTime(dtNow), now.toDateTime(dtNow));
    }

    /**
     * Returns a string describing 'time' as a time relative to 'now'.
     *
     * @see #getRelativeTimeSpanString(Context, ReadableInstant, ReadableInstant, int)
     */
    public static CharSequence getRelativeTimeSpanString(Context context, ReadableInstant time, ReadableInstant now) {
        int flags = FORMAT_SHOW_DATE | FORMAT_SHOW_YEAR | FORMAT_ABBREV_MONTH;
        return getRelativeTimeSpanString(context, time, now, flags);
    }

    /**
     * Returns a string describing 'time' as a time relative to 'now'.
     *
     * See {@link android.text.format.DateUtils#getRelativeTimeSpanString} for full docs.
     *
     * @param context the context
     * @param time the time to describe
     * @param now the current time
     * @param flags a bit mask for formatting options
     * @return a string describing 'time' as a time relative to 'now'.
     */
    public static CharSequence getRelativeTimeSpanString(Context context, ReadableInstant time, ReadableInstant now,
                                                         int flags) {
        boolean abbrevRelative = (flags & (FORMAT_ABBREV_RELATIVE | FORMAT_ABBREV_ALL)) != 0;

        boolean past = !now.isBefore(time);
        Interval interval = past ? new Interval(time, now) : new Interval(now, time);

        int resId;
        long count;
        if (Minutes.minutesIn(interval).isLessThan(Minutes.ONE)) {
            count = Seconds.secondsIn(interval).getSeconds();
            if (past) {
                if (abbrevRelative) {
                    resId = R.plurals.abbrev_num_seconds_ago;
                }
                else {
                    resId = R.plurals.num_seconds_ago;
                }
            }
            else {
                if (abbrevRelative) {
                    resId = R.plurals.abbrev_in_num_seconds;
                }
                else {
                    resId = R.plurals.in_num_seconds;
                }
            }
        }
        else if (Hours.hoursIn(interval).isLessThan(Hours.ONE)) {
            count = Minutes.minutesIn(interval).getMinutes();
            if (past) {
                if (abbrevRelative) {
                    resId = R.plurals.abbrev_num_minutes_ago;
                }
                else {
                    resId = R.plurals.num_minutes_ago;
                }
            }
            else {
                if (abbrevRelative) {
                    resId = R.plurals.abbrev_in_num_minutes;
                }
                else {
                    resId = R.plurals.in_num_minutes;
                }
            }
        }
        else if (Days.daysIn(interval).isLessThan(Days.ONE)) {
            count = Hours.hoursIn(interval).getHours();
            if (past) {
                if (abbrevRelative) {
                    resId = R.plurals.abbrev_num_hours_ago;
                }
                else {
                    resId = R.plurals.num_hours_ago;
                }
            }
            else {
                if (abbrevRelative) {
                    resId = R.plurals.abbrev_in_num_hours;
                }
                else {
                    resId = R.plurals.in_num_hours;
                }
            }
        }
        else if (Weeks.weeksIn(interval).isLessThan(Weeks.ONE)) {
            count = Days.daysIn(interval).getDays();
            if (past) {
                if (abbrevRelative) {
                    resId = R.plurals.abbrev_num_days_ago;
                }
                else {
                    resId = R.plurals.num_days_ago;
                }
            }
            else {
                if (abbrevRelative) {
                    resId = R.plurals.abbrev_in_num_days;
                }
                else {
                    resId = R.plurals.in_num_days;
                }
            }
        }
        else {
            return formatDateRange(context, time, time, flags);
        }

        String format = context.getResources().getQuantityString(resId, (int) count);
        return String.format(format, count);
    }

    /**
     * Returns a relative time string to display the time expressed by millis.
     *
     * Missing fields from 'time' are filled in with values from the current time.
     *
     * See {@link android.text.format.DateUtils#getRelativeTimeSpanString} for full docs.
     *
     * @param withPreposition If true, the string returned will include the correct
     * preposition ("at 9:20am", "on 10/12/2008" or "on May 29").
     */
    public static CharSequence getRelativeTimeSpanString(Context ctx, ReadablePartial time, boolean withPreposition) {
        return getRelativeTimeSpanString(ctx, time.toDateTime(DateTime.now()), withPreposition);
    }

    /**
     * Returns a relative time string to display the time expressed by millis.
     *
     * See {@link android.text.format.DateUtils#getRelativeTimeSpanString} for full docs.
     *
     * @param withPreposition If true, the string returned will include the correct
     * preposition ("at 9:20am", "on 10/12/2008" or "on May 29").
     */
    public static CharSequence getRelativeTimeSpanString(Context ctx, ReadableInstant time, boolean withPreposition) {
        String result;
        LocalDate now = LocalDate.now();
        LocalDate timeDate = new LocalDate(time);

        int prepositionId;
        if (Days.daysBetween(now, timeDate).getDays() == 0) {
            // Same day
            int flags = FORMAT_SHOW_TIME;
            result = formatDateRange(ctx, time, time, flags);
            prepositionId = R.string.preposition_for_time;
        }
        else if (Years.yearsBetween(now, timeDate).getYears() != 0) {
            // Different years
            int flags = FORMAT_SHOW_DATE | FORMAT_SHOW_YEAR | FORMAT_NUMERIC_DATE;
            result = formatDateRange(ctx, time, time, flags);

            // This is a date (like "10/31/2008" so use the date preposition)
            prepositionId = R.string.preposition_for_date;
        }
        else {
            // Default
            int flags = FORMAT_SHOW_DATE | FORMAT_ABBREV_MONTH;
            result = formatDateRange(ctx, time, time, flags);
            prepositionId = R.string.preposition_for_date;
        }

        if (withPreposition) {
            result = ctx.getString(prepositionId, result);
        }

        return result;
    }
}