<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE helpset
  PUBLIC "-//Sun Microsystems Inc.//DTD JavaHelp HelpSet Version 1.0//EN"
         "http://java.sun.com/products/javahelp/helpset_1_0.dtd">
<helpset version="1.0" xml:lang="en-US">
   <!-- title -->
   <title>Help</title>

   <!-- maps -->
   <maps>
     <homeID>item000</homeID>
     <mapref location="default/help_map.jhm" />
   </maps>

   <!-- views -->
   <view>
      <name>TOC</name>
      <label>Table Of Contents</label>
      <type>javax.help.TOCView</type>
      <data>default/help_toc.xml</data>
   </view>

   <view xml:lang="en-US">
     <name>Search</name>
     <label>Search</label>
     <type>javax.help.SearchView</type>
     <data engine="com.sun.java.help.search.DefaultSearchEngine">JavaHelpSearch</data>
  </view>

</helpset>
