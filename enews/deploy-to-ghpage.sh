cd html-generator-node && npm install
node app.js

mkdir output
mv enews.html output/index.html
git add output && git commit -m "deploy html"

cd `git rev-parse --show-toplevel`

git subtree push --prefix enews/html-generator-node/output origin gh-pages
