<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN"
         "http://java.sun.com/products/javahelp/helpset_1_0.dtd">
<helpset version="1.0" xml:lang="cz-CZ">
   <!-- title -->
   <title>Nápověda</title>

   <!-- maps -->
   <maps>
     <homeID>item000</homeID>
     <mapref location="cz/help_map.jhm" />
   </maps>

   <!-- views -->
   <view>
      <name>TOC</name>
      <label>Obsah</label>
      <type>javax.help.TOCView</type>
      <data>cz/help_toc.xml</data>
   </view>

   <view xml:lang="cz-CZ">
     <name>Search</name>
     <label>Vyhledávání</label>
     <type>javax.help.SearchView</type>
     <data engine="com.sun.java.help.search.DefaultSearchEngine">JavaHelpSearch/cz</data>
  </view>

  <presentation default="true">
      <size width="800" height="600" />
      <image>image001</image>
  </presentation>

</helpset>
