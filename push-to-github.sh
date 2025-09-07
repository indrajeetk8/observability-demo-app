#!/bin/bash

# Push to GitHub Script for indrajeetk8
echo "🚀 Pushing Observability Demo to GitHub"
echo "========================================"
echo "Profile: https://github.com/indrajeetk8"
echo "Repository: observability-demo-app"
echo

# Navigate to project directory
cd /home/indrajeet/observability-demo-app

# Check current git status
echo "📊 Current Git Status:"
echo "======================"
git log --oneline -3 2>/dev/null || echo "No commits found"
echo
git status --short
echo

# Check if remote origin exists
if git remote get-url origin &>/dev/null; then
    echo "✅ Git remote already configured:"
    git remote -v
else
    echo "🔧 Adding GitHub remote..."
    git remote add origin https://github.com/indrajeetk8/observability-demo-app.git
fi
echo

# Commit any uncommitted changes
if [[ -n $(git status --porcelain) ]]; then
    echo "📝 Committing recent changes..."
    git add .
    git commit -m "Final updates before GitHub push"
    echo
fi

# Show what will be pushed
echo "📦 Files ready to push:"
echo "======================="
find . -type f -not -path './.git/*' | wc -l
echo "Total files ready for upload"
echo

# Push to GitHub
echo "📤 Pushing to GitHub..."
echo "======================="
echo "Target: https://github.com/indrajeetk8/observability-demo-app"
echo

git push -u origin main

# Check if push was successful
if [ $? -eq 0 ]; then
    echo
    echo "🎉 SUCCESS! Repository pushed to GitHub!"
    echo "========================================"
    echo
    echo "🌟 Your repository is now live at:"
    echo "   https://github.com/indrajeetk8/observability-demo-app"
    echo
    echo "📊 What's included in your repository:"
    echo "   • Complete Spring Boot application with observability"
    echo "   • Jenkins CI/CD pipeline with security scanning"
    echo "   • Docker containerization with best practices"
    echo "   • Grafana Cloud integration (Loki, Tempo, Prometheus)"
    echo "   • Comprehensive documentation and quick start guide"
    echo "   • Professional-grade code with testing and quality gates"
    echo
    echo "📝 Next Steps:"
    echo "   1. Visit your repository: https://github.com/indrajeetk8/observability-demo-app"
    echo "   2. Add repository topics for better discoverability"
    echo "   3. Pin this repository to your profile if desired"
    echo "   4. Share with your network!"
    echo
    echo "🎯 Perfect for portfolio, learning, and professional development!"
    echo
else
    echo
    echo "❌ Push failed!"
    echo "==============="
    echo
    echo "Possible reasons:"
    echo "1. Repository doesn't exist on GitHub yet"
    echo "2. Authentication issues"
    echo "3. Network connectivity problems"
    echo
    echo "💡 Solutions:"
    echo "1. Make sure you created the repository 'observability-demo-app' on GitHub"
    echo "2. Go to https://github.com/indrajeetk8 and click 'New repository'"
    echo "3. Use repository name: observability-demo-app"
    echo "4. Don't initialize with README (we already have it)"
    echo "5. Try running this script again after creating the repository"
    echo
    echo "📋 Or manually run:"
    echo "   git push -u origin main"
fi

echo
echo "🔗 GitHub Profile: https://github.com/indrajeetk8"
echo "📧 Need help? Check PUSH_TO_GITHUB.md for detailed instructions"
