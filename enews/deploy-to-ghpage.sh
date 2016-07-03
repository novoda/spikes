mkdir output
mv enews.html output/index.html
git add output && git commit -m "deploy enews html"

cd `git rev-parse --show-toplevel`

git subtree push --prefix enews/html-generator-node/output origin gh-pages
