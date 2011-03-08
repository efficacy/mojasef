No source directories are provided by default.  If you need them, create the following 
optional structure:

|- delivery		stuff which will end up in a delivered jar, war or zip
| |
| |- files		directory structure to be copied directly into delivered archive
| |- java		directory structure of compilable files, typically starts com/..
| 
|-test			stuff used to test compilerd or deployed code
| |
| |- files		directory structure to be copied directly into test directories
| |- java		directory structure of compilable test files, typically starts test/..
| 
|-filter		directory structure of files templated using the "filter" tokens 
|			specified in "project.prp" and patched over the copied "delivery" and
|			"test" trees before compilation and archiving
|			
|-style			directory structure of files processed using the XML stylesheet 
			"default.xsl" (after processing the "filter" tree, if present)
			and patched over the copied "delivery" and "test" trees.