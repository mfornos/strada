<%@tag description="Layout template" pageEncoding="UTF-8"%>
<%@attribute name="header" fragment="true" %>
<%@attribute name="footer" fragment="true" %>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>   
    
<!doctype html>

<!--[if lt IE 7 ]> <html class="ie ie6 no-js" lang="en"> <![endif]-->
<!--[if IE 7 ]>    <html class="ie ie7 no-js" lang="en"> <![endif]-->
<!--[if IE 8 ]>    <html class="ie ie8 no-js" lang="en"> <![endif]-->
<!--[if IE 9 ]>    <html class="ie ie9 no-js" lang="en"> <![endif]-->
<!--[if gt IE 9]><!--><html class="no-js" lang="en"><!--<![endif]-->
<!-- the "no-js" class is for Modernizr. -->

  <head id="www-sitename-com" data-template-set="html5-reset">

  <meta charset="utf-8">
  
  <!-- Always force latest IE rendering engine (even in intranet) & Chrome Frame -->
  <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
  
  <title>Strada Analytics Sample</title>
  
  <meta name="title" content="">
  <meta name="description" content="">
  <!-- Google will often use this as its description of your page/site. Make it good. -->
  
  <meta name="google-site-verification" content="">
  <!-- Speaking of Google, don't forget to set your site up: http://google.com/webmasters -->
  
  <meta name="author" content="Your Name Here">
  <meta name="Copyright" content="Copyright Your Name Here 2011. All Rights Reserved.">

  <!-- Dublin Core Metadata : http://dublincore.org/ -->
  <meta name="DC.title" content="Project Name">
  <meta name="DC.subject" content="What you're about.">
  <meta name="DC.creator" content="Who made this site.">
  
  <!--  Mobile Viewport Fix
  j.mp/mobileviewport & davidbcalhoun.com/2010/viewport-metatag 
  device-width : Occupy full width of the screen in its current orientation
  initial-scale = 1.0 retains dimensions instead of zooming out if page height > device height
  maximum-scale = 1.0 retains dimensions instead of zooming in if page width < device width
  -->
  <!-- Uncomment to use; use thoughtfully!
  <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
  -->

  <link rel="shortcut icon" href="/favicon.ico">
  <!-- This is the traditional favicon.
     - size: 16x16 or 32x32
     - transparency is OK
     - see wikipedia for info on browser support: http://mky.be/favicon/ -->
     
  <link rel="apple-touch-icon" href="/_/img/apple-touch-icon.png">
  <!-- The is the icon for iOS's Web Clip.
     - size: 57x57 for older iPhones, 72x72 for iPads, 114x114 for iPhone4's retina display (IMHO, just go ahead and use the biggest one)
     - To prevent iOS from applying its styles to the icon name it thusly: apple-touch-icon-precomposed.png
     - Transparency is not recommended (iOS will put a black BG behind the icon) -->
  
  <!-- CSS: screen, mobile & print are all in the same file -->
  <link rel="stylesheet" href="/_/css/bootstrap.min.css">
  <link rel="stylesheet" href="/_/css/styles.css">
  <link rel="stylesheet" media="screen" type="text/css" href="/_/js/datepicker/css/base.css" /> 
  <link rel="stylesheet" media="screen" type="text/css" href="/_/js/datepicker/css/clean.css" /> 
  
  <link rel="stylesheet" media="screen" type="text/css" href="/_/js/jquery/ui/css/smoothness/jquery-ui-1.9.2.custom.min.css" />
  <link rel="stylesheet" media="screen" type="text/css" href="/_/js/multiselect/css/ui.multiselect.css" />  
  
  <!-- all our JS is at the bottom of the page, except for Modernizr. -->
  <script src="/_/js/modernizr-1.7.min.js"></script>
  <script src="/_/js/jquery-1.8.3.min.js"></script>
  <script type="text/javascript" src="/_/js/jquery/ui/js/jquery-ui-1.9.2.custom.min.js"></script>
  <script type="text/javascript" src="/_/js/multiselect/js/ui.multiselect.js"></script>
  <script type="text/javascript" src="/_/js/highcharts.js"></script>
  <script src="/_/js/bootstrap.min.js"></script>
  <script type="text/javascript" src="/_/js/datepicker/js/datepicker.js"></script>  
  <script type="text/javascript" src="/_/js/funnel.js"></script>
  <script type="text/javascript" src="/_/js/app.js"></script>
  <script type="text/javascript" src="/_/js/strada.example.js"></script>
  <script>
    StatsTracker.post(window.location.pathname, {"one":1,"two":"es"});
  </script>
  </head>
  <body>
    <header id="pageheader" class="jumbotron">
      <div class="container">
        <jsp:invoke fragment="header"/>
      </div>
    </header>
    <div class="container">
      <jsp:doBody/>
    </div>
    <footer id="pagefooter">
      <div class="container">
        <jsp:invoke fragment="footer"/>
      </div>
    </footer>
  </body>
</html>
