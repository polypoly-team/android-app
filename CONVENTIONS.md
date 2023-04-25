## ACTIVITIES
#### Structure
To make the previews and the code more clear/clean, please create a method in your `NameActivity` named `NameContent` so that the method `onCreate` doesn't contain any UI logic. Why? So that all our activities will have the same structure and all the non-UI logic that is executed at activity launch isn't mixed.
Here is an example:

    class NameActivity : ComponentActivity() {
	    override fun onCreate(..) {
		    ...
		    setContent { NameContent() }
	    }

		@Preview
		@Composable
		fun NamePreview() { NameContent() }
		
		// =============================================
		
		@Composable
		fun NameContent() {...}

#### MenuActivity
In the same fashion, if your activity extends `MenuActivity`, do as follows (with `MenuContent`):

    class NameActivity : MenuActivity("name") {
	    override fun onCreate(..) {
		    ...
		    setContent { 
			    MenuContent { NameContent() }
			}
	    }

		@Preview
		@Composable
		fun NamePreview() { NameContent() }
		
		// =============================================
		
		@Composable
		fun NameContent() {...}


## NAMING
#### Variables, parameters, classes
In all the project we use ***camelCase***

#### Tags, content description
However, when you want to add tags, test_tags, descriptions, please use ***snake_case***

