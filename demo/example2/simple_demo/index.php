<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Simple Demo</title>
    <style type="text/css">
      .h {
       background-color:FF0;
       font-weight:bold;
      }
    </style>
  </head>

  <body>
    <div>
<?php	

	$sleepingTime = 1000;

	if($_GET['q'] != '') {
		if($_GET['q']%2 == 0){
			
			usleep($sleepingTime);
			echo 'Sleeping time: ' . $sleepingTime . " micro seconds";
		} else {
			echo 'No sleeping time';		
		}
	} else {
		echo 'No parameter set';
	}
?>

    </div>
  </body>
</html>
