package at.fhj.utils.xml;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;



/**
 * @author Ilya Boyandin
 *
 * $Revision: 1.1 $
 */
public class LightXMLWriter {
  
  private BufferedWriter out;
  private int indentLevel = 0;
  private String indent = "  ";
  private List stack;
  private boolean inOpenTag = false;
  private boolean newLine = false;
    private String charsetName;
    
    public LightXMLWriter(Writer writer, String charsetName) {
        this.charsetName = charsetName;
        if (writer instanceof BufferedWriter) {
            out = (BufferedWriter)writer;
        } else {
            out = new BufferedWriter(writer);
        }
        stack = new ArrayList();
    }
    
    public LightXMLWriter(OutputStream os, String charsetName) throws UnsupportedEncodingException {
        this(new OutputStreamWriter(os, charsetName), charsetName);
    }
    
    public LightXMLWriter(String outputFile) throws UnsupportedEncodingException, FileNotFoundException {
        this(outputFile, "UTF-8");
    }
  
  public LightXMLWriter(String outputFile, String charsetName) throws UnsupportedEncodingException, FileNotFoundException {
        this(new OutputStreamWriter(new FileOutputStream(outputFile), charsetName), charsetName);
  }

  public void setIndent(String indent) {
    this.indent = indent;
  }

  public void startDocument() throws IOException {
     out.write("<?xml version=\"1.0\" encoding=\"" + charsetName + "\"?>"); nl();
  }

  public void endDocument() throws IOException {
    while (stack.size() > 0) {
      tagClose();
    }
    out.flush();
    out.close();
  }

  public void tagOpen(String tag) throws IOException {
    if (inOpenTag) {
      out.write(">"); nl();
    }
    stack.add(tag);
    indent();
    out.write("<");  out.write(tag);
    indentLevel++;
    inOpenTag = true;
    newLine = false;
  }

  public void attr(String name, String value) throws IOException {
    if (!inOpenTag) {
      throw new RuntimeException("Wrong state for attribute writing: not in a open tag");      
    }
    out.write(' '); out.write(name);
    out.write("=\""); out.write(escape(value)); out.write('\"'); 
    newLine = false;
  }

  public void text(String text) throws IOException {
    if (inOpenTag) {
      out.write(">");
      inOpenTag = false;

      text = escape(text);
      out.write(text);
      newLine = (text.endsWith("\n"));
    } else {
      indent(); nl();
      indent(); out.write(escape(text)); nl();
    }
  }
  
  public void tagClose() throws IOException {
    if (stack.size() == 0) {
      throw new RuntimeException("No tag to close");
    }
    String tag = (String)stack.remove(stack.size() - 1);
    indentLevel--;
    if (inOpenTag) {
      out.write(" />"); nl();
    } else {
      indent();
      out.write("</"); out.write(tag); out.write(">"); nl();
    }
    inOpenTag = false;
  }
  
  public void nl() throws IOException {
    out.newLine();
    newLine = true;
  }

  private void indent() throws IOException {
    if (newLine) {
      indent(indentLevel);
    }
  }

  private void indent(int level) throws IOException {
    for (int i = 0; i < level; i++) {
      out.write(indent);
    }
  }
  
  public static String escape(String value) {
    StringBuffer r = null;
    int len = value.length(), p = 0;
    char ch;
    while (p < len) {
      ch = value.charAt(p);
      String s = null;
      switch (ch) {
      case '"':
        s = "&quot;";
        break;
      case '<':
        s = "&lt;";
        break;
      case '>':
        s = "&gt;";
        break;
      }
      if (s != null) {
        if (r == null) {
          r = new StringBuffer(len + len / 3);
          for (int i = 0; i < p; i++) r.append(value.charAt(i));
        }
        r.append(s);
      } else {
        if (r != null) {
          r.append(ch);
        }
      }
      p++;
    }
    return (r != null ? r.toString() : value);
  }

}
