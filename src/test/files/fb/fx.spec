http.application $= stubs.FallbackTestApplication
template.source.aa $= org.stringtree.fetcher.FallbackFetcher
template.source.aa + $= org.stringtree.fetcher.TractURLFetcher src/test/files/fb/aa/
template.source.aa + $= org.stringtree.fetcher.TractURLFetcher src/test/files/fb/common/
template.source.bb $= org.stringtree.fetcher.FallbackFetcher
template.source.bb + $= org.stringtree.fetcher.TractURLFetcher src/test/files/fb/bb/
template.source.bb + $= org.stringtree.fetcher.TractURLFetcher src/test/files/fb/common/
