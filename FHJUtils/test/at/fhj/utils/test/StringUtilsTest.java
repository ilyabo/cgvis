package at.fhj.utils.test;

import junit.framework.TestCase;
import at.fhj.utils.misc.StringUtils;


/**
 *
 * @author Ilya Boyandin
 *
 * $Revision: 1.2 $
 */
public class StringUtilsTest extends TestCase {

  public void testReplaceAll() {
    assertEquals("THAT is a funny thing, because THAT thing is so funny!",
        StringUtils.replaceAll(
            "this is a funny thing, because this thing is so funny!",
            "this", "THAT")
    );
    assertEquals("Nothing",  StringUtils.replaceAll("Nothing", "!!!", "..."));
    assertEquals("!ab!!cde!!!fghi!!j!kl!",
        StringUtils.replaceAll(".ab..cde...fghi..j.kl.", ".", "!"));
    assertEquals("!!!!!!..", StringUtils.replaceAll("........", "...", "!!!"));
    assertEquals("ab!!!!!!ab", StringUtils.replaceAll("yz!!!!!!yz", "yz", "ab"));
    assertEquals("", StringUtils.replaceAll("something", "something", ""));
    assertEquals("", StringUtils.replaceAll("somesome", "some", ""));
    assertEquals("somesome", StringUtils.replaceAll("somenonesome", "none", ""));
    assertEquals("somesome", StringUtils.replaceAll("nonesomenonesomenone", "none", ""));
    assertEquals("something", StringUtils.replaceAll("something", "", "nothing"));
    assertEquals("", StringUtils.replaceAll("", "something", "nothing"));
  }


  public void testCommonPrefix() {
    assertEquals("", StringUtils.commonPrefix("hello", "world!"));
    assertEquals("hel", StringUtils.commonPrefix("hello", "help!"));
    assertEquals("That", StringUtils.commonPrefix("That", "That"));
  }


  public void testCountLines() {
    assertEquals(5, StringUtils.countLines("test\nthis\nline\ncount\n\n"));
    assertEquals(5, StringUtils.countLines("test\rthis\rline\rcount\r\r"));
    assertEquals(1, StringUtils.countLines("hello"));
    assertEquals(0, StringUtils.countLines(""));
    assertEquals(2, StringUtils.countLines("1234512345", 5));
    assertEquals(4, StringUtils.countLines("1234512345123456", 5));
    assertEquals(4, StringUtils.countLines("12345123451234\n56", 5));
  }


  public void testCutLinesOff() {
    assertEquals("test", StringUtils.cutLinesOff("test", 1));
    assertEquals("12345\n...", StringUtils.cutLinesOff("123456", 1, 5));
    assertEquals("test\nthis\nline\n...", StringUtils.cutLinesOff("test\nthis\nline\ncount\n\n", 3));
    assertEquals("test\nthis\nline", StringUtils.cutLinesOff("test\nthis\nline", 3));
    assertEquals("test\nthis\nline\ncount\n\n", StringUtils.cutLinesOff("test\nthis\nline\ncount\n\n", 5));
    assertEquals("test\nthis\nline\ncount\n...", StringUtils.cutLinesOff("test\nthis\nline\ncount\n\na", 4));
    assertEquals("", StringUtils.cutLinesOff("test\nthis\nline", 0));
  }

  public void testGetCommonPrefix() {
    assertEquals("abc", StringUtils.getCommonPrefix(new String[] {"abc", "abcd", "abcdef"}));
    assertEquals("", StringUtils.getCommonPrefix(new String[] {"abc", "abcd", "abcdef", ""}));
    assertEquals("ab", StringUtils.getCommonPrefix(new String[] {"abc", "abbc", "abcd"}));
    assertEquals("", StringUtils.getCommonPrefix(new String[] {""}));
    assertEquals("", StringUtils.getCommonPrefix(new String[] {""}));
  }
}
