git checkout gh-pages
cd `git rev-parse --show-toplevel`
mv enews/html-generator-node/enews.html index.html
git add index.html && git commit -m "deploy enews html"
git push -f origin gh-pages
