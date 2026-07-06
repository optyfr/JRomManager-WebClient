package jrm.webui.client.utils;

/**
 * A mutable string wrapper that provides case-insensitive equality, hashing,
 * and region matching.
 *
 * <p>Two instances are considered equal if their underlying data strings are
 * equal ignoring case. The hash code is derived from the lower-cased value so
 * that equal instances produce the same hash. The {@link #startsWith} and
 * {@link #endsWith} methods also perform case-insensitive comparisons.</p>
 *
 * @since 2.5
 */
public class CaseInsensitiveString {
    /** The underlying string data. */
    private String dataString;

    /**
     * Creates a new case-insensitive string wrapper.
     *
     * @param dataString the initial string value
     */
    public CaseInsensitiveString(String dataString) {
        this.dataString = dataString;
    }

    /**
     * Returns the underlying string data.
     *
     * @return the string data
     */
    public String getDataString() {
        return this.dataString;
    }

    /**
     * Sets the underlying string data.
     *
     * @param dataString the new string value
     */
    public void setDataString(String dataString) {
        this.dataString = dataString;
    }

    /**
     * Returns a hash code based on the lower-cased value.
     *
     * @return the hash code, or {@code 0} if the data string is {@code null}
     */
    @Override
    public int hashCode() {
        return dataString == null ? 0 : dataString.toLowerCase().hashCode();
    }

    /**
     * Indicates whether some other object is "equal to" this one, ignoring case.
     *
     * @param obj the reference object with which to compare
     * @return {@code true} if the other object is a {@code CaseInsensitiveString}
     *         with an equal data string (ignoring case)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof CaseInsensitiveString))
            return false;
        CaseInsensitiveString is = (CaseInsensitiveString) obj;
        if (is.dataString == null)
            return this.dataString == null;
        return is.dataString.equalsIgnoreCase(this.dataString);
    }

    /**
     * Tests whether this string starts with the given prefix, ignoring case.
     *
     * @param prefix the prefix to test
     * @return {@code true} if this string starts with the prefix (case-insensitive)
     */
    public boolean startsWith(String prefix) {
        if (dataString == null || prefix == null)
            return (dataString == null && prefix == null);
        if (prefix.length() > dataString.length())
            return false;
        return dataString.regionMatches(true, 0, prefix, 0, prefix.length());
    }

    /**
     * Tests whether this string ends with the given suffix, ignoring case.
     *
     * @param suffix the suffix to test
     * @return {@code true} if this string ends with the suffix (case-insensitive)
     */
    public boolean endsWith(String suffix) {
        if (dataString == null || suffix == null)
            return (dataString == null && suffix == null);
        if (suffix.length() > dataString.length())
            return false;
        return dataString.regionMatches(true, dataString.length() - suffix.length(), suffix, 0, suffix.length());
    }

    /**
     * Returns the underlying string data.
     *
     * @return the string data
     */
    @Override
    public String toString() {
        return dataString;
    }
}