http.application$=org.stringtree.mojasef.apps.LiteralURLRouter src/test/files/mount.spec
#http.application$=Present
#
# example of an indirect reference to another entry
link.to.timestamp~ = system.timestamp
#
# example of a live date with a format
just.the.date$=org.stringtree.finder.live.LiveDate dd/MMM/yyyy
#
# example of a page class setting, used when rendering using templating
page.class.static = bordered
#
templates.incontext=WooHoo->'${this}'
blogservice$=BlogService
