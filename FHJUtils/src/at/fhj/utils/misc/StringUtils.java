package at.fhj.utils.misc;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

  /**
   * Returns a string which is the same as str, but with the first letter uppercased.
   *
   * @param str
   * @return
   */
  public static String firstUpper(final String str) {
    return str.substring(0, 1).toUpperCase() + str.substring(1);
  }

  /**
   * Replaces all occurences of what in where with replacement. This function doesn't use the
   * regular expressions API, so it works with the older Java versions as well.
   *
   * @return
   */
  public static String replaceAll(String where, String what, String replacement) {
    if (what.length() == 0) {
      return where;
    }
    StringBuffer sb = null;
    int pos = 0, ppos = -1;
    while ((pos = where.indexOf(what, pos)) > -1) {
      if (sb == null) {
        sb = new StringBuffer(where.length() + (replacement.length() - what.length()));
      }
      final int start = (ppos > -1 ? ppos + what.length() : 0);
      sb.append(where.substring(start, pos));
      sb.append(replacement);
      ppos = pos;
      pos += what.length();
    }
    if (ppos > -1 && ppos < (where.length() - what.length())) {
      sb.append(where.substring(ppos + what.length()));
    }
    return (sb != null ? sb.toString() : where);
  }

  /**
   * Returns the next available name, adding a number at the end of the <code>nameBase</code>.
   *
   * @param nameBase
   *          Must not contain regexp control symbols
   * @param existingNames
   *          Names which are no more available (it's not necessary that they match the same pattern
   *          as the generated names)
   * @return
   */
  public static String getEnumeratedName(String nameBase, List existingNames) {
    final Pattern p = Pattern.compile(nameBase + "([0-9]+)?");
    int maxNum = 0;
    for (int i = 0; i < existingNames.size(); i++) {
      final String r = (String) existingNames.get(i);
      final Matcher m = p.matcher(r);
      if (m.matches()) {
        final String sNum = m.group(1);
        final int num;
        if (sNum != null) {
          num = Integer.parseInt(sNum);
        } else {
          num = 1;
        }
        if (num > maxNum)
          maxNum = num;
      }
    }
    if (maxNum > 0)
      return nameBase + (maxNum + 1);
    else
      return nameBase;
  }

  public static String commonPrefix(String s1, String s2) {
    StringBuffer sb = null;
    int i = 0;
    while (i < s1.length() && i < s2.length() && s1.charAt(i) == s2.charAt(i)) {
      if (sb == null) {
        sb = new StringBuffer(Math.min(s1.length(), s2.length()));
      }
      sb.append(s1.charAt(i));
      i++;
    }
    return (sb != null ? sb.toString() : "");
  }

  public static int countLines(String str) {
    return countLines(str, -1);
  }

  public static int countLines(String str, int wrapLength) {
    final int len = str.length();
    int lines = 0;
    int lineLen = 0;
    int pos = 0;
    while (pos < len) {
      final char ch = str.charAt(pos);
      lineLen++;
      if ((wrapLength > 0 && lineLen >= wrapLength) || (ch == '\n' || ch == '\r')) {
        lines++;
        lineLen = 0;
      }
      pos++;
    }
    if (lineLen > 0) {
      lines++;
    }
    return lines;
  }

  public static String cutLinesOff(String str, int maxLinesCount) {
    return cutLinesOff(str, maxLinesCount, -1);
  }

  public static String cutLinesOff(String str, int maxLinesCount, int wrapLength) {
    if (maxLinesCount <= 0) {
      return "";
    }
    final int len = str.length();
    int lines = 0;
    int lineLen = 0;
    int pos = 0;
    while (pos < len) {
      final char ch = str.charAt(pos);
      lineLen++;
      if ((wrapLength > 0 && lineLen >= wrapLength) || (ch == '\n' || ch == '\r')) {
        lines++;
        if (lines >= maxLinesCount && pos < len - 1) {
          if (ch == '\n' || ch == '\r') {
            return str.substring(0, pos + 1) + "...";
          } else {
            return str.substring(0, pos + 1) + "\n...";
          }
        }
        lineLen = 0;
      }
      pos++;
    }
    return str;
  }

  public static String join(String[] sa, String delimiter) {
    StringBuilder sb = new StringBuilder();
    for (int i = 0, len = sa.length; i < len; i++) {
      sb.append(sa[i]);
      if (i < len - 1)
        sb.append(delimiter);
    }
    return sb.toString();
  }

  public static String join(Iterable<String> it, String delimiter) {
    StringBuilder sb = new StringBuilder();
    boolean first = true;
    for (String s : it) {
      if (!first) {
        sb.append(delimiter);
      } else {
        first = false;
      }
      sb.append(s);
    }
    return sb.toString();
  }

  public static String getCommonPrefix(String[] sa) {
    StringBuilder common = new StringBuilder();
    int pos = 0;
    outer: while (true) {
      boolean first = true;
      char c = 0;
      for (String s : sa) {
        if (pos >= s.length()) {
          break outer;
        }
        char ch = s.charAt(pos);
        if (first) {
          c = ch;
          first = false;
        } else {
          if (ch != c) {
            break outer;
          }
        }
      }
      if (first) {  // means that the 'iterables' is empty
        break outer;
      }
      common.append(c);
      pos++;
    }
    return common.toString();
  }

}
