
// 版本质量分析报表JavaScript
class VersionQualityReport {
    constructor() {
        this.apiBase = '/versionQualityReport';
        this.currentProjectId = null;
        this.currentVersion = null;
        this.charts = {};
        this.init();
    }

    init() {
        this.loadProjects();
        this.bindEvents();
    }

    bindEvents() {
        document.getElementById('projectSelect').addEventListener('change', (e) => {
            this.currentProjectId = e.target.value;
            this.loadVersions();
            this.clearReport();
        });

        document.getElementById('versionSelect').addEventListener('change', (e) => {
            this.currentVersion = e.target.value;
        });
    }

    // 加载项目列表
    async loadProjects() {
        try {
            const response = await fetch('/project/list');
            const result = await response.json();
            const select = document.getElementById('projectSelect');
            
            select.innerHTML = '<option value="">请选择项目</option>';
            result.data.forEach(project => {
                const option = document.createElement('option');
                option.value = project.id;
                option.textContent = project.projectName;
                select.appendChild(option);
            });
        } catch (error) {
            console.error('加载项目列表失败:', error);
        }
    }

    // 加载版本列表
    async loadVersions() {
        if (!this.currentProjectId) return;

        try {
            const response = await fetch(`/release/getByProject/${this.currentProjectId}`);
            const result = await response.json();
            const versionSelect = document.getElementById('versionSelect');
            const compareSelect = document.getElementById('compareVersionSelect');
            
            versionSelect.innerHTML = '<option value="">请选择版本</option>';
            compareSelect.innerHTML = '<option value="">选择对比版本</option>';
            
            if (result.success && result.data) {
                result.data.forEach(release => {
                    const option1 = document.createElement('option');
                    const option2 = document.createElement('option');
                    option1.value = option2.value = release.version;
                    option1.textContent = option2.textContent = release.version;
                    versionSelect.appendChild(option1);
                    compareSelect.appendChild(option2);
                });
            }
        } catch (error) {
            console.error('加载版本列表失败:', error);
        }
    }

    // 生成质量报表
    async loadReport() {
        if (!this.currentProjectId || !this.currentVersion) {
            alert('请先选择项目和版本！');
            return;
        }

        try {
            // 显示加载状态
            this.showLoading();

            // 并行加载所有数据
            const [
                defectDensityResp,
                testCoverageResp,
                defectDistributionResp,
                executionRateResp
            ] = await Promise.all([
                fetch(`${this.apiBase}/getDefectDensity/${this.currentProjectId}/${this.currentVersion}`),
                fetch(`${this.apiBase}/getTestCoverage/${this.currentProjectId}/${this.currentVersion}`),
                fetch(`${this.apiBase}/getDefectDistribution/${this.currentProjectId}/${this.currentVersion}`),
                fetch(`${this.apiBase}/getExecutionRate/${this.currentProjectId}/${this.currentVersion}`)
            ]);

            const [
                defectDensityData,
                testCoverageData,
                defectDistributionData,
                executionRateData
            ] = await Promise.all([
                defectDensityResp.json(),
                testCoverageResp.json(),
                defectDistributionResp.json(),
                executionRateResp.json()
            ]);

            // 更新指标卡片
            this.updateMetricCards(defectDensityData, testCoverageData, executionRateData);

            // 更新图表
            this.updateCharts(defectDistributionData, executionRateData);

            // 更新详情表格
            this.updateDetailTables(testCoverageData, executionRateData);

            // 隐藏对比区域
            document.getElementById('versionComparisonSection').style.display = 'none';

        } catch (error) {
            console.error('生成报表失败:', error);
            alert('生成报表失败，请检查网络连接');
        } finally {
            this.hideLoading();
        }
    }

    // 更新指标卡片
    updateMetricCards(defectDensityData, testCoverageData, executionRateData) {
        if (defectDensityData.success) {
            const data = defectDensityData.data;
            document.getElementById('defectDensity').textContent = data.defectDensity || '0';
            document.getElementById('defectDensityLevel').textContent = data.densityLevel || '-';
            document.getElementById('defectDensityLevel').className = 
                'level-badge ' + this.getLevelClass(data.densityLevel);
        }

        if (testCoverageData.success) {
            const data = testCoverageData.data;
            document.getElementById('testCoverage').textContent = 
                (data.storyCoverage || 0) + '%';
            document.getElementById('testCoverageLevel').textContent = data.coverageLevel || '-';
            document.getElementById('testCoverageLevel').className = 
                'level-badge ' + this.getLevelClass(data.coverageLevel);
        }

        if (executionRateData.success) {
            const data = executionRateData.data;
            document.getElementById('executionRate').textContent = 
                (data.executionRate || 0) + '%';
            document.getElementById('executionRateLevel').textContent = data.executionLevel || '-';
            document.getElementById('executionRateLevel').className = 
                'level-badge ' + this.getLevelClass(data.executionLevel);

            document.getElementById('passRate').textContent = 
                (data.passRate || 0) + '%';
            document.getElementById('passRateLevel').textContent = 
                this.getPassRateLevel(data.passRate || 0);
            document.getElementById('passRateLevel').className = 
                'level-badge ' + this.getLevelClass(this.getPassRateLevel(data.passRate || 0));
        }
    }

    // 更新图表
    updateCharts(defectDistributionData, executionRateData) {
        if (defectDistributionData.success) {
            this.updateDefectSeverityChart(defectDistributionData.data.severityDistribution);
            this.updateModuleDefectChart(defectDistributionData.data.moduleDistribution);
            this.updateEnvDefectChart(defectDistributionData.data.envDistribution);
            this.updateDefectTrendChart(defectDistributionData.data.defectTrend);
        }

        if (executionRateData.success) {
            this.updateExecutionTrendChart(executionRateData.data.cycleExecutionDetails);
        }
    }

    // 更新缺陷严重程度饼图
    updateDefectSeverityChart(severityData) {
        const ctx = document.getElementById('defectSeverityChart').getContext('2d');
        
        if (this.charts.defectSeverity) {
            this.charts.defectSeverity.destroy();
        }

        const labels = Object.keys(severityData || {});
        const data = Object.values(severityData || {});

        this.charts.defectSeverity = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: [
                        '#FF6384',
                        '#36A2EB',
                        '#FFCE56',
                        '#4BC0C0',
                        '#9966FF'
                    ]
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }

    // 更新模块缺陷柱图
    updateModuleDefectChart(moduleData) {
        const ctx = document.getElementById('moduleDefectChart').getContext('2d');
        
        if (this.charts.moduleDefect) {
            this.charts.moduleDefect.destroy();
        }

        const labels = Object.keys(moduleData || {});
        const data = Object.values(moduleData || {});

        this.charts.moduleDefect = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: '缺陷数量',
                    data: data,
                    backgroundColor: 'rgba(54, 162, 235, 0.8)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    // 更新环境缺陷对比图
    updateEnvDefectChart(envData) {
        const ctx = document.getElementById('envDefectChart').getContext('2d');
        
        if (this.charts.envDefect) {
            this.charts.envDefect.destroy();
        }

        const labels = Object.keys(envData || {});
        const data = Object.values(envData || {});

        this.charts.envDefect = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: labels,
                datasets: [{
                    data: data,
                    backgroundColor: [
                        '#FF9F40',
                        '#4BC0C0',
                        '#36A2EB'
                    ]
                }]
            },
            options: {
                responsive: true,
                plugins: {
                    legend: {
                        position: 'bottom'
                    }
                }
            }
        });
    }

    // 更新执行趋势图
    updateExecutionTrendChart(cycleData) {
        const ctx = document.getElementById('executionTrendChart').getContext('2d');
        
        if (this.charts.executionTrend) {
            this.charts.executionTrend.destroy();
        }

        const labels = (cycleData || []).map(cycle => cycle.cycleName);
        const executionRates = (cycleData || []).map(cycle => cycle.executionRate);
        const passRates = (cycleData || []).map(cycle => cycle.passRate);

        this.charts.executionTrend = new Chart(ctx, {
            type: 'line',
            data: {
                labels: labels,
                datasets: [{
                    label: '执行率',
                    data: executionRates,
                    borderColor: 'rgba(75, 192, 192, 1)',
                    backgroundColor: 'rgba(75, 192, 192, 0.2)',
                    tension: 0.1
                }, {
                    label: '通过率',
                    data: passRates,
                    borderColor: 'rgba(255, 99, 132, 1)',
                    backgroundColor: 'rgba(255, 99, 132, 0.2)',
                    tension: 0.1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100
                    }
                }
            }
        });
    }

    // 更新详情表格
    updateDetailTables(testCoverageData, executionRateData) {
        // 更新故事覆盖详情
        if (testCoverageData.success) {
            this.updateStoryCoverageDetails(testCoverageData.data.storyCoverageDetails);
        }

        // 更新测试周期执行详情
        if (executionRateData.success) {
            this.updateCycleExecutionTable(executionRateData.data.cycleExecutionDetails);
        }
    }

    // 更新故事覆盖详情
    updateStoryCoverageDetails(storyCoverageDetails) {
        const container = document.getElementById('storyCoverageDetails');
        
        if (!storyCoverageDetails || storyCoverageDetails.length === 0) {
            container.innerHTML = '<div class="text-center text-muted"><i class="bi bi-info-circle"></i><p>暂无故事数据</p></div>';
            return;
        }

        let html = '';
        storyCoverageDetails.forEach(story => {
            const coverClass = story.isCovered ? 'covered' : 'not-covered';
            const icon = story.isCovered ? 'check-circle' : 'x-circle';
            html += `
                <div class="story-coverage-item ${coverClass}">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="bi bi-${icon}"></i>
                            <strong>${story.storyTitle}</strong>
                        </div>
                        <div>
                            <span class="badge bg-secondary">${story.testCaseCount} 个用例</span>
                        </div>
                    </div>
                </div>
            `;
        });
        
        container.innerHTML = html;
    }

    // 更新测试周期执行表格
    updateCycleExecutionTable(cycleExecutionDetails) {
        const tbody = document.querySelector('#cycleExecutionTable tbody');
        
        if (!cycleExecutionDetails || cycleExecutionDetails.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted"><i class="bi bi-info-circle"></i> 暂无测试周期数据</td></tr>';
            return;
        }

        let html = '';
        cycleExecutionDetails.forEach(cycle => {
            html += `
                <tr>
                    <td>${cycle.cycleName}</td>
                    <td>${cycle.planned}</td>
                    <td>${cycle.executed}</td>
                    <td>${cycle.passed}</td>
                    <td>${cycle.failed}</td>
                    <td><span class="badge ${this.getExecutionBadgeClass(cycle.executionRate)}">${cycle.executionRate}%</span></td>
                    <td><span class="badge ${this.getPassBadgeClass(cycle.passRate)}">${cycle.passRate}%</span></td>
                </tr>
            `;
        });
        
        tbody.innerHTML = html;
    }

    // 版本对比分析
    async loadComparison() {
        const compareVersion = document.getElementById('compareVersionSelect').value;
        if (!this.currentProjectId || !this.currentVersion || !compareVersion) {
            alert('请选择项目、当前版本和对比版本！');
            return;
        }

        try {
            const response = await fetch(
                `${this.apiBase}/getVersionComparison/${this.currentProjectId}?startVersion=${this.currentVersion}&endVersion=${compareVersion}`
            );
            const result = await response.json();

            if (result.success) {
                this.updateVersionComparisonTable(result.data.comparisonData);
                document.getElementById('versionComparisonSection').style.display = 'block';
            }
        } catch (error) {
            console.error('版本对比失败:', error);
            alert('版本对比失败，请检查网络连接');
        }
    }

    // 更新版本对比表格
    updateVersionComparisonTable(comparisonData) {
        const tbody = document.querySelector('#versionComparisonTable tbody');
        
        let html = '';
        comparisonData.forEach(versionData => {
            const qualityLevel = this.calculateQualityLevel(versionData);
            html += `
                <tr>
                    <td><strong>${versionData.version}</strong></td>
                    <td>${versionData.defectDensity}</td>
                    <td>${versionData.testCoverage}%</td>
                    <td>${versionData.executionRate}%</td>
                    <td>${versionData.passRate}%</td>
                    <td>${versionData.totalDefects}</td>
                    <td>${versionData.totalTestCases}</td>
                    <td><span class="level-badge ${this.getLevelClass(qualityLevel)}">${qualityLevel}</span></td>
                </tr>
            `;
        });
        
        tbody.innerHTML = html;
    }

    // 辅助方法
    getLevelClass(level) {
        switch(level) {
            case '优秀': return 'level-excellent';
            case '良好': return 'level-good';
            case '一般': return 'level-average';
            case '需改进': return 'level-poor';
            default: return 'level-average';
        }
    }

    getPassRateLevel(passRate) {
        if (passRate >= 95) return '优秀';
        else if (passRate >= 80) return '良好';
        else if (passRate >= 60) return '一般';
        else return '需改进';
    }

    getExecutionBadgeClass(rate) {
        if (rate >= 90) return 'bg-success';
        else if (rate >= 70) return 'bg-warning';
        else return 'bg-danger';
    }

    getPassBadgeClass(rate) {
        if (rate >= 95) return 'bg-success';
        else if (rate >= 80) return 'bg-info';
        else if (rate >= 60) return 'bg-warning';
        else return 'bg-danger';
    }

    calculateQualityLevel(versionData) {
        // 综合评估版本质量等级
        const defectDensity = parseFloat(versionData.defectDensity);
        const testCoverage = parseFloat(versionData.testCoverage);
        const passRate = parseFloat(versionData.passRate);
        
        let score = 0;
        if (defectDensity < 5) score += 25;
        else if (defectDensity < 10) score += 20;
        else if (defectDensity < 20) score += 15;
        else score += 10;
        
        if (testCoverage >= 95) score += 25;
        else if (testCoverage >= 80) score += 20;
        else if (testCoverage >= 60) score += 15;
        else score += 10;
        
        if (passRate >= 95) score += 25;
        else if (passRate >= 80) score += 20;
        else if (passRate >= 60) score += 15;
        else score += 10;
        
        if (score >= 70) return '优秀';
        else if (score >= 60) return '良好';
        else if (score >= 50) return '一般';
        else return '需改进';
    }

    // 显示加载状态
    showLoading() {
        // 可以添加加载动画
        document.querySelectorAll('.metric-number').forEach(el => {
            el.textContent = '...';
        });
    }

    // 隐藏加载状态
    hideLoading() {
        // 加载完成后的处理
    }

    // 清空报表
    clearReport() {
        document.querySelectorAll('.metric-number').forEach(el => {
            el.textContent = '-';
        });
        document.querySelectorAll('.level-badge').forEach(el => {
            el.textContent = '-';
            el.className = 'level-badge level-average';
        });
        
        // 销毁所有图表
        Object.values(this.charts).forEach(chart => {
            if (chart) chart.destroy();
        });
        this.charts = {};
        
        // 清空详情区域
        document.getElementById('storyCoverageDetails').innerHTML = 
            '<div class="text-center text-muted"><i class="bi bi-info-circle"></i><p>请选择版本查看故事覆盖详情</p></div>';
        
        document.querySelector('#cycleExecutionTable tbody').innerHTML = 
            '<tr><td colspan="7" class="text-center text-muted"><i class="bi bi-info-circle"></i> 请选择版本查看测试周期详情</td></tr>';
        
        document.getElementById('versionComparisonSection').style.display = 'none';
    }

    // 导出报表
    exportReport() {
        if (!this.currentProjectId || !this.currentVersion) {
            alert('请先生成报表！');
            return;
        }
        
        // TODO: 实现导出功能
        alert('导出功能开发中...');
    }
}

// 初始化版本质量分析报表
const qualityReport = new VersionQualityReport();
