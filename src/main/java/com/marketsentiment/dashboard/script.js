// ===============================
// API URLS
// ===============================

const API_URL = "http://localhost:8080/api/news";
const ANALYZE_URL = "http://localhost:8080/api/analyze";


// ===============================
// GLOBAL VARIABLES
// ===============================

let newsData = [];
let filteredNews = [];
let sentimentChart = null;


// ===============================
// PAGE LOAD
// ===============================

document.addEventListener("DOMContentLoaded", () => {

    loadNews();

    setupEventListeners();

});


// ===============================
// EVENT LISTENERS
// ===============================

function setupEventListeners() {

    // Refresh button

    const refreshButton =
        document.getElementById("refreshButton");

    if (refreshButton) {

        refreshButton.addEventListener(
            "click",
            loadNews
        );

    }


    // Company filter

    const companyFilter =
        document.getElementById("companyFilter");

    if (companyFilter) {

        companyFilter.addEventListener(
            "change",
            filterNews
        );

    }


    // Analyze button

    const analyzeButton =
        document.getElementById("analyzeButton");

    if (analyzeButton) {

        analyzeButton.addEventListener(
            "click",
            analyzeHeadline
        );

    }


    // Allow Enter key in headline input

    const headlineInput =
        document.getElementById("headlineInput");

    if (headlineInput) {

        headlineInput.addEventListener(
            "keydown",
            function (event) {

                if (event.key === "Enter") {

                    analyzeHeadline();

                }

            }
        );

    }

}


// ===============================
// LOAD NEWS
// ===============================

async function loadNews() {

    const loadingMessage =
        document.getElementById("loadingMessage");

    const newsContainer =
        document.getElementById("newsContainer");


    if (loadingMessage) {

        loadingMessage.style.display = "block";

    }


    try {

        const response =
            await fetch(API_URL);


        if (!response.ok) {

            throw new Error(
                "Failed to load news"
            );

        }


        newsData =
            await response.json();


        if (!Array.isArray(newsData)) {

            throw new Error(
                "Invalid data received from API"
            );

        }


        createCompanyFilter();


        filterNews();


    } catch (error) {

        console.error(
            "Error loading news:",
            error
        );


        if (newsContainer) {

            newsContainer.innerHTML = `
                <div class="analysis-error">
                    ❌ Unable to load market news.
                    <br><br>
                    Make sure your Java API is running.
                </div>
            `;

        }

    } finally {

        if (loadingMessage) {

            loadingMessage.style.display = "none";

        }

    }

}


// ===============================
// CREATE COMPANY FILTER
// ===============================

function createCompanyFilter() {

    const companyFilter =
        document.getElementById("companyFilter");


    if (!companyFilter) {

        return;

    }


    const companies =
        [
            ...new Set(
                newsData.map(
                    news => news.company
                )
            )
        ];


    companyFilter.innerHTML =
        `<option value="ALL">
            All Companies
        </option>`;


    companies.forEach(
        company => {

            const option =
                document.createElement("option");

            option.value = company;

            option.textContent = company;

            companyFilter.appendChild(
                option
            );

        }
    );

}


// ===============================
// FILTER NEWS
// ===============================

function filterNews() {

    const companyFilter =
        document.getElementById("companyFilter");


    const selectedCompany =
        companyFilter
            ? companyFilter.value
            : "ALL";


    if (
        selectedCompany === "ALL"
        ||
        !selectedCompany
    ) {

        filteredNews =
            [...newsData];

    } else {

        filteredNews =
            newsData.filter(
                news =>
                    news.company ===
                    selectedCompany
            );

    }


    displayNews();

    updateDashboard();

}


// ===============================
// DISPLAY NEWS
// ===============================

function displayNews() {

    const newsContainer =
        document.getElementById(
            "newsContainer"
        );


    if (!newsContainer) {

        return;

    }


    if (filteredNews.length === 0) {

        newsContainer.innerHTML = `
            <div class="loading">
                No news available.
            </div>
        `;

        return;

    }


    newsContainer.innerHTML =
        filteredNews
            .map(
                news =>
                    createNewsCard(news)
            )
            .join("");

}


// ===============================
// CREATE NEWS CARD
// ===============================

function createNewsCard(news) {

    const sentiment =
        (news.sentiment || "NEUTRAL")
            .toUpperCase();


    let badgeClass =
        "sentiment-neutral";


    if (sentiment === "POSITIVE") {

        badgeClass =
            "sentiment-positive";

    } else if (
        sentiment === "NEGATIVE"
    ) {

        badgeClass =
            "sentiment-negative";

    }


    const score =
        Number(news.score) || 0;


    return `

        <div class="news-card">

            <div class="news-company">
                ${escapeHTML(news.company)}
            </div>


            <div class="news-title">
                ${escapeHTML(news.title)}
            </div>


            <span class="
                sentiment-badge
                ${badgeClass}
            ">
                ${sentiment}
            </span>


            <div class="score-container">

                <div class="score-label">

                    <span>
                        Sentiment Score
                    </span>

                    <strong>
                        ${score}%
                    </strong>

                </div>


                <div class="score-bar">

                    <div
                        class="score-fill"
                        style="
                            width: ${score}%;
                            background:
                            ${
                                sentiment === "POSITIVE"
                                    ? "#16a34a"
                                    : sentiment === "NEGATIVE"
                                        ? "#dc2626"
                                        : "#6b7280"
                            };
                        "
                    ></div>

                </div>

            </div>

        </div>

    `;

}


// ===============================
// UPDATE DASHBOARD
// ===============================

function updateDashboard() {

    const total =
        filteredNews.length;


    let positive = 0;
    let negative = 0;
    let neutral = 0;


    filteredNews.forEach(
        news => {

            const sentiment =
                (news.sentiment || "NEUTRAL")
                    .toUpperCase();


            if (sentiment === "POSITIVE") {

                positive++;

            } else if (
                sentiment === "NEGATIVE"
            ) {

                negative++;

            } else {

                neutral++;

            }

        }
    );


    const positivePercentage =
        total
            ? Math.round(
                (positive / total) * 100
            )
            : 0;


    const negativePercentage =
        total
            ? Math.round(
                (negative / total) * 100
            )
            : 0;


    const neutralPercentage =
        total
            ? Math.round(
                (neutral / total) * 100
            )
            : 0;


    // Update cards

    setText(
        "positiveCount",
        positive
    );

    setText(
        "positivePercentage",
        `${positivePercentage}%`
    );


    setText(
        "negativeCount",
        negative
    );

    setText(
        "negativePercentage",
        `${negativePercentage}%`
    );


    setText(
        "neutralCount",
        neutral
    );

    setText(
        "neutralPercentage",
        `${neutralPercentage}%`
    );


    // Overall trend

    let overallTrend =
        "NEUTRAL";


    if (
        positive > negative
        &&
        positive > neutral
    ) {

        overallTrend =
            "POSITIVE";

    } else if (
        negative > positive
        &&
        negative > neutral
    ) {

        overallTrend =
            "NEGATIVE";

    }


    setText(
        "overallTrend",
        overallTrend
    );


    updateChart(
        positive,
        negative,
        neutral
    );

}


// ===============================
// UPDATE CHART
// ===============================

function updateChart(
    positive,
    negative,
    neutral
) {

    const canvas =
        document.getElementById(
            "sentimentChart"
        );


    if (!canvas) {

        return;

    }


    const ctx =
        canvas.getContext("2d");


    if (sentimentChart) {

        sentimentChart.destroy();

    }


    sentimentChart =
        new Chart(
            ctx,
            {

                type: "doughnut",

                data: {

                    labels: [
                        "Positive",
                        "Negative",
                        "Neutral"
                    ],

                    datasets: [

                        {

                            data: [
                                positive,
                                negative,
                                neutral
                            ]

                        }

                    ]

                },

                options: {

                    responsive: true,

                    maintainAspectRatio: false,

                    plugins: {

                        legend: {

                            position: "bottom"

                        }

                    }

                }

            }
        );

}


// ===============================
// ANALYZE NEW HEADLINE
// ===============================

async function analyzeHeadline() {

    const headlineInput =
        document.getElementById(
            "headlineInput"
        );

    const analyzeButton =
        document.getElementById(
            "analyzeButton"
        );

    const analysisResult =
        document.getElementById(
            "analysisResult"
        );

    const analysisLoading =
        document.getElementById(
            "analysisLoading"
        );


    if (!headlineInput) {

        return;

    }


    const headline =
        headlineInput.value.trim();


    // Check empty input

    if (!headline) {

        analysisResult.innerHTML = `

            <div class="analysis-error">

                ⚠️ Please enter a headline first.

            </div>

        `;

        return;

    }


    // Show loading

    if (analysisLoading) {

        analysisLoading.style.display =
            "block";

    }


    if (analysisResult) {

        analysisResult.innerHTML = "";

    }


    if (analyzeButton) {

        analyzeButton.disabled = true;

        analyzeButton.textContent =
            "Analyzing...";

    }


    try {

        const response =
            await fetch(
                ANALYZE_URL,
                {

                    method: "POST",

                    headers: {

                        "Content-Type":
                            "application/json"

                    },

                    body: JSON.stringify({

                        headline: headline

                    })

                }
            );


        const data =
            await response.json();


        if (!response.ok) {

            throw new Error(
                data.error ||
                "Analysis failed"
            );

        }


        displayAnalysisResult(data);


    } catch (error) {

        console.error(
            "Analysis error:",
            error
        );


        if (analysisResult) {

            analysisResult.innerHTML = `

                <div class="analysis-error">

                    ❌ Unable to analyze headline.

                    <br><br>

                    Make sure your Java API is running.

                </div>

            `;

        }

    } finally {

        if (analysisLoading) {

            analysisLoading.style.display =
                "none";

        }


        if (analyzeButton) {

            analyzeButton.disabled =
                false;

            analyzeButton.textContent =
                "Analyze Sentiment";

        }

    }

}


// ===============================
// DISPLAY ANALYSIS RESULT
// ===============================

function displayAnalysisResult(data) {

    const analysisResult =
        document.getElementById(
            "analysisResult"
        );


    if (!analysisResult) {

        return;

    }


    const sentiment =
        (data.sentiment || "NEUTRAL")
            .toUpperCase();


    const score =
        Number(data.score) || 0;


    let resultClass =
        "analysis-neutral";


    let emoji =
        "➖";


    if (sentiment === "POSITIVE") {

        resultClass =
            "analysis-positive";

        emoji =
            "📈";

    } else if (
        sentiment === "NEGATIVE"
    ) {

        resultClass =
            "analysis-negative";

        emoji =
            "📉";

    }


    analysisResult.innerHTML = `

        <div class="
            analysis-card
            ${resultClass}
        ">

            <h3>
                Analysis Result
            </h3>


            <p class="analysis-headline">

                <strong>Headline:</strong>

                ${escapeHTML(
                    data.headline
                )}

            </p>


            <div class="analysis-sentiment">

                ${emoji}
                ${sentiment}

            </div>


            <div class="analysis-score">

                Sentiment Score:
                ${score}%

            </div>

        </div>

    `;

}


// ===============================
// AUTO REFRESH
// ===============================

setInterval(
    loadNews,
    30000
);


// ===============================
// HELPER FUNCTIONS
// ===============================

function setText(
    elementId,
    value
) {

    const element =
        document.getElementById(
            elementId
        );


    if (element) {

        element.textContent =
            value;

    }

}


function escapeHTML(text) {

    if (text === null ||
        text === undefined) {

        return "";

    }


    return String(text)
        .replace(
            /&/g,
            "&amp;"
        )
        .replace(
            /</g,
            "&lt;"
        )
        .replace(
            />/g,
            "&gt;"
        )
        .replace(
            /"/g,
            "&quot;"
        )
        .replace(
            /'/g,
            "&#039;"
        );

}