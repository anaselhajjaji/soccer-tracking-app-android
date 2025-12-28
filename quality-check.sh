#!/bin/bash

# Colors for output
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

echo ""
echo -e "${BLUE}🔍 Running Quality Checks for Soccer Tracker${NC}"
echo "================================================="
echo ""

# Clean build
echo -e "${YELLOW}📦 Cleaning build...${NC}"
./gradlew clean --quiet

# Run tests
echo -e "${YELLOW}🧪 Running tests...${NC}"
if ./gradlew test --quiet; then
    echo -e "${GREEN}✅ Tests passed${NC}"
else
    echo -e "\033[0;31m❌ Tests failed${NC}"
    exit 1
fi

# Generate coverage
echo -e "${YELLOW}📊 Generating coverage report...${NC}"
./gradlew jacocoTestReport --quiet
if [ -f "app/build/reports/jacoco/jacocoTestReport/html/index.html" ]; then
    echo -e "${GREEN}✅ Coverage report generated${NC}"
else
    echo -e "\033[0;31m⚠️  Coverage report not generated${NC}"
fi

# Run lint
echo -e "${YELLOW}🔎 Running lint analysis...${NC}"
./gradlew lintDebug --quiet
if [ -f "app/build/reports/lint-results-debug.html" ]; then
    echo -e "${GREEN}✅ Lint report generated${NC}"
else
    echo -e "\033[0;31m⚠️  Lint report not generated${NC}"
fi

# Run detekt
echo -e "${YELLOW}🔍 Running Detekt static analysis...${NC}"
./gradlew detekt --quiet
if [ -f "app/build/reports/detekt/detekt.html" ]; then
    echo -e "${GREEN}✅ Detekt report generated${NC}"
else
    echo -e "\033[0;31m⚠️  Detekt report not generated${NC}"
fi

echo ""
echo -e "${GREEN}✅ Quality checks complete!${NC}"
echo ""
echo "📊 Reports available:"
echo "-------------------"

if [ -f "app/build/reports/tests/testDebugUnitTest/index.html" ]; then
    echo "  • Test Results:  app/build/reports/tests/testDebugUnitTest/index.html"
fi

if [ -f "app/build/reports/jacoco/jacocoTestReport/html/index.html" ]; then
    echo "  • Coverage:      app/build/reports/jacoco/jacocoTestReport/html/index.html"
fi

if [ -f "app/build/reports/lint-results-debug.html" ]; then
    echo "  • Lint:          app/build/reports/lint-results-debug.html"
fi

if [ -f "app/build/reports/detekt/detekt.html" ]; then
    echo "  • Detekt:        app/build/reports/detekt/detekt.html"
fi

echo ""
echo -e "${BLUE}Opening reports in browser...${NC}"

# Open reports in browser
if [ -f "app/build/reports/jacoco/jacocoTestReport/html/index.html" ]; then
    open app/build/reports/jacoco/jacocoTestReport/html/index.html 2>/dev/null || \
    xdg-open app/build/reports/jacoco/jacocoTestReport/html/index.html 2>/dev/null
fi

if [ -f "app/build/reports/lint-results-debug.html" ]; then
    open app/build/reports/lint-results-debug.html 2>/dev/null || \
    xdg-open app/build/reports/lint-results-debug.html 2>/dev/null
fi

if [ -f "app/build/reports/tests/testDebugUnitTest/index.html" ]; then
    open app/build/reports/tests/testDebugUnitTest/index.html 2>/dev/null || \
    xdg-open app/build/reports/tests/testDebugUnitTest/index.html 2>/dev/null
fi

if [ -f "app/build/reports/detekt/detekt.html" ]; then
    open app/build/reports/detekt/detekt.html 2>/dev/null || \
    xdg-open app/build/reports/detekt/detekt.html 2>/dev/null
fi

echo ""
echo -e "${GREEN}Done! 🎉${NC}"
echo ""
