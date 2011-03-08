# example mount configuration showing the operation of the other example applications
# file types, could also be loaded from a file
filetype.html = text/html
filetype.htm = text/html
filetype.jpg = image/jpeg
filetype.jpeg = image/jpeg
filetype.jpe = image/jpeg
filetype.gif = image/gif
filetype.png = image/png
filetype.txt = text/plain
filetype.tract = text/plain
filetype.spec = text/plain
filetype.java = text/plain
#
# user/password mapping, used by LoginExample
login.users$ = org.stringtree.finder.SpecFileFetcher src/test/files/users.spec
#
# default user if not logged in
user.name = guest

auth/    org.stringtree.mojasef.apps.SessionWrapper  org.stringtree.mojasef.examples.LoginExample login.users
dynamic/ org.stringtree.mojasef.apps.TemplateServer  dynamic
static/  org.stringtree.mojasef.apps.FileTreeServer  .
method/  org.stringtree.mojasef.examples.MethodExample
session/ org.stringtree.mojasef.apps.SessionWrapper  org.stringtree.mojasef.examples.MethodExample
present  Present
