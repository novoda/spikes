mkdir output
mv enews.html output/index.html
git add output && git commit -m "deploy enews html"

cd `git rev-parse --show-toplevel`

git push origin `git subtree split --prefix enews/html-generator-node/output gh-pages`:gh-pages --force