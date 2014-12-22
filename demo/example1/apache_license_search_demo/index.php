<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <title>Apache License search</title>
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
	// many files for good timing measurements
        $lics[0]['name'] = 'Apache License, Version 1.0';
        $lics[0]['file'] = 'LICENSE-1.0.txt';
        $lics[1]['name'] = 'Apache License, Version 1.1';
        $lics[1]['file'] = 'LICENSE-1.1.txt';
        $lics[2]['name'] = 'Apache License, Version 2.0';
        $lics[2]['file'] = 'LICENSE-2.0.txt';
        $lics[3]['name'] = 'GNU GENERAL PUBLIC LICENSE - Version 1, February 1989';
        $lics[3]['file'] = 'gpl-1.0.txt';
        $lics[4]['name'] = 'GNU GENERAL PUBLIC LICENSE - Version 2, June 1991';
        $lics[4]['file'] = 'gpl-2.0.txt';
	$lics[5]['name'] = 'GNU GENERAL PUBLIC LICENSE - Version 3, 29 June 2007';
	$lics[5]['file'] = 'gpl-3.0.txt';
        $lics[6]['name'] = 'GNU LESSER GENERAL PUBLIC LICENSE';
        $lics[6]['file'] = 'lgpl.txt';
        $lics[7]['name'] = 'GNU AFFERO GENERAL PUBLIC LICENSE';
        $lics[7]['file'] = 'agpl.txt';
        $lics[8]['name'] = 'GNU Free Documentation License';
        $lics[8]['file'] = 'fdl.txt';

	if(isset($_GET['q']) && $_GET['q'] != '') {
		foreach($lics as $lic) {
			$licText = file_get_contents($lic['file']);
			$licText = preg_replace('/' . preg_quote($_GET['q']) . '/', '<span class="h">' . $_GET['q'] . '</span>', $licText, 1, $numResults);

			if($numResults != 0) {
				echo '<h1>' . $lic['name'] . '</h1>';
				echo '<pre>' . $licText . '</pre>';
				break;
			}
		}
		if($numResults == 0) {
			echo '<h1>Error: could not find anything.</h1>';
		}
	} else {
		echo '<h1>Open Source License Search</h1>';
		echo 'You can search through: <ul>';
		foreach($lics as $lic) {
			echo '<li>' . $lic['name'] . '</li>';
		}
		echo '</ul>';
		echo '<form method="get">Search: <input name="q" type="text" size="30" maxlength="30"><input type="submit" value=" GO! "></form>';
	}
?>

    </div>
  </body>
</html>
