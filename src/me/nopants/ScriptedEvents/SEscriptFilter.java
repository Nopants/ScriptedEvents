package me.nopants.ScriptedEvents;

import java.io.File;
import java.io.FilenameFilter;

public class SEscriptFilter implements FilenameFilter
{
  public boolean accept( File f, String s )
  {
    return s.toLowerCase().endsWith( ".script" );
  }
}
