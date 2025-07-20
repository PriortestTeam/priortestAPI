// 版本质量分析报表JavaScript
class VersionQualityReport {
    constructor() {
        this.apiBase = '/api/versionQualityReport';
        this.currentProjectId = null;
        this.currentVersion = null;
        this.compareVersion = null;
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

        document.getElementById('compareVersionSelect').addEventListener('change', (e) => {
            this.compareVersion = e.target.value;
        });
    }

    // 加载项目列表  
    async loadProjects() {
        console.log('开始加载项目列表...');
        try {
            const response = await fetch('/project/getProjectList');
            console.log('项目列表响应状态:', response.status);

            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }

            const result = await response.json();
            console.log('项目列表数据:', result);

            const select = document.getElementById('projectSelect');
            if (!select) {
                console.error('找不到项目选择器元素');
                return;
            }

            select.innerHTML = '<option value="">请选择项目</option>';
            if (result.data && Array.isArray(result.data)) {
                console.log('找到', result.data.length, '个项目');
                result.data.forEach(project => {
                    const option = document.createElement('option');
                    option.value = project.id;
                    option.textContent = project.projectName || project.name;
                    select.appendChild(option);
                });
            } else {
                console.warn('项目数据格式不正确或为空:', result);
                // 添加测试项目
                const testOption = document.createElement('option');
                testOption.value = 'test-project-1';
                testOption.textContent = '测试项目（演示用）';
                select.appendChild(testOption);
            }
        } catch (error) {
            console.error('加载项目列表失败:', error);
            this.showError('加载项目列表失败: ' + error.message);

            // 添加备用项目选项
            const select = document.getElementById('projectSelect');
            if (select) {
                select.innerHTML = `
                    <option value="">请选择项目</option>
                    <option value="demo-project">演示项目</option>
                `;
            }
        }
    }

    // 加载版本列表
    async loadVersions() {
        if (!this.currentProjectId) return;

        try {
            const response = await fetch(`/api/project/${this.currentProjectId}/versions`);
            const result = await response.json();
            const versionSelect = document.getElementById('versionSelect');
            const compareSelect = document.getElementById('compareVersionSelect');

            versionSelect.innerHTML = '<option value="">请选择版本</option>';
            compareSelect.innerHTML = '<option value="">选择对比版本</option>';

            if (result.data && Array.isArray(result.data)) {
                result.data.forEach(version => {
                    const option1 = document.createElement('option');
                    option1.value = version;
                    option1.textContent = version;
                    versionSelect.appendChild(option1);

                    const option2 = document.createElement('option');
                    option2.value = version;
                    option2.textContent = version;
                    compareSelect.appendChild(option2);
                });
            }
        } catch (error) {
            console.error('加载版本列表失败:', error);
            this.showError('加载版本列表失败');
        }
    }

    // 生成质量报表
    async loadReport() {
        if (!this.currentProjectId || !this.currentVersion) {
            this.showError('请先选择项目和版本');
            return;
        }

        this.showLoading(true);

        try {
            // 并行加载所有数据
            const [
                defectDensityResp,
                testCoverageResp,
                defectDistributionResp,
                executionRateResp,
                releasePhaseResp
            ] = await Promise.all([
                fetch(`${this.apiBase}/getDefectDensity/${this.currentProjectId}/${this.currentVersion}`),
                fetch(`${this.apiBase}/getTestCoverage/${this.currentProjectId}/${this.currentVersion}`),
                fetch(`${this.apiBase}/getDefectDistribution/${this.currentProjectId}/${this.currentVersion}`),
                fetch(`${this.apiBase}/getExecutionRate/${this.currentProjectId}/${this.currentVersion}`),
                fetch(`${this.apiBase}/getPostReleaseDefects/${this.currentProjectId}/${this.currentVersion}`)
            ]);

            const defectDensityData = await defectDensityResp.json();
            const testCoverageData = await testCoverageResp.json();
            const defectDistributionData = await defectDistributionResp.json();
            const executionRateData = await executionRateResp.json();
            const releasePhaseData = await releasePhaseResp.json();

            // 更新指标卡片
            this.updateMetricsCards(defectDensityData.data, testCoverageData.data, executionRateData.data);

            // 更新发布阶段指标
            this.updateReleasePhaseMetrics(releasePhaseData.data);

            // 更新图表
            this.updateCharts(defectDistributionData.data, executionRateData.data, defectDensityData.data);
            
            // 更新发布阶段图表
            this.updateReleasePhaseCharts(releasePhaseData.data);

            // 更新故事覆盖详情
            this.updateStoryCoverageDetails(testCoverageData.data.storyCoverageDetails || []);

            // 更新测试周期执行详情
            this.updateCycleExecutionTable(executionRateData.data.cycleExecutionDetails || []);

            // 更新质量洞察和改进建议
            this.updateQualityInsights(releasePhaseData.data);

        } catch (error) {
            console.error('加载报表数据失败:', error);
            this.showError('加载报表数据失败');
        } finally {
            this.showLoading(false);
        }
    }

    // 版本对比
    async loadComparison() {
        if (!this.currentProjectId || !this.currentVersion || !this.compareVersion) {
            this.showError('请选择项目和两个对比版本');
            return;
        }

        this.showLoading(true);

        try {
            const response = await fetch(`${this.apiBase}/getVersionComparison/${this.currentProjectId}?startVersion=${this.currentVersion}&endVersion=${this.compareVersion}`);
            const result = await response.json();

            if (result.success && result.data) {
                this.updateVersionComparisonTable(result.data.comparisonData || []);
                document.getElementById('versionComparisonSection').style.display = 'block';
            }

        } catch (error) {
            console.error('加载版本对比失败:', error);
            this.showError('加载版本对比失败');
        } finally {
            this.showLoading(false);
        }
    }

    // 更新指标卡片
    updateMetricsCards(defectData, coverageData, executionData) {
        // 缺陷密度
        this.updateDefectDensity(defectData);

        // 测试覆盖率
        this.updateMetricCard('testCoverage', (coverageData.storyCoverage || 0).toFixed(1) + '%', coverageData.level || '-');

        // 测试执行率
        this.updateMetricCard('executionRate', (executionData.executionRate || 0).toFixed(1) + '%', executionData.executionLevel || '-');

        // 测试通过率
        this.updateMetricCard('passRate', (executionData.passRate || 0).toFixed(1) + '%', executionData.passLevel || '-');
    }

    updateMetricCard(metricId, value, level) {
        const valueEl = document.getElementById(metricId);
        const levelEl = document.getElementById(metricId + 'Level');
        const cardEl = document.getElementById(metricId + 'Card');

        if (valueEl) valueEl.textContent = value;
        if (levelEl) {
            levelEl.textContent = level;
            levelEl.className = 'level-badge ' + this.getLevelClass(level);
        }

        // 动态设置卡片颜色
        if (cardEl) {
            // 移除所有等级相关的类
            cardEl.className = cardEl.className.replace(/level-\w+/g, '').replace(/metric-card\s+/g, '');
            // 添加基础类和新的等级类
            cardEl.className = 'metric-card ' + this.getCardLevelClass(level);
        }
    }

    getLevelClass(level) {
        switch (level) {
            case '优秀': return 'level-excellent';
            case '良好': return 'level-good';
            case '一般': return 'level-average';
            case '需改进': return 'level-poor';
            default: return 'level-average';
        }
    }

    getCardLevelClass(level) {
        switch (level) {
            case '优秀': return 'level-excellent';
            case '良好': return 'level-good';
            case '一般': return 'level-average';
            case '需改进': return 'level-poor';
            default: return 'level-default';
        }
    }

    // 更新图表
    updateCharts(defectDistData, executionData, defectDensityData) {
        // 缺陷严重程度分布饼图
        if (defectDistData.severityDistribution) {
            this.createDefectSeverityChart(defectDistData.severityDistribution);
        }

        // 测试执行趋势图
        if (executionData.executionTrend) {
            this.createExecutionTrendChart(executionData.executionTrend);
        }

        // 模块缺陷分布柱状图
        if (defectDensityData.moduleDefectDistribution) {
            this.createModuleDefectChart(defectDensityData.moduleDefectDistribution);
        }

        // 环境缺陷对比图
        if (defectDistData.envDefectDistribution) {
            this.createEnvDefectChart(defectDistData.envDefectDistribution);
        }
    }

    createDefectSeverityChart(data) {
        const ctx = document.getElementById('defectSeverityChart').getContext('2d');

        if (this.charts.severity) {
            this.charts.severity.destroy();
        }

        this.charts.severity = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: data.map(item => item.severity),
                datasets: [{
                    data: data.map(item => item.count),
                    backgroundColor: data.map(item => item.color),
                    borderWidth: 2,
                    borderColor: '#fff'
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

    createExecutionTrendChart(data) {
        const ctx = document.getElementById('executionTrendChart').getContext('2d');

        if (this.charts.trend) {
            this.charts.trend.destroy();
        }

        this.charts.trend = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data.map(item => item.date),
                datasets: [{
                    label: '执行率',
                    data: data.map(item => item.executionRate),
                    borderColor: '#007bff',
                    backgroundColor: 'rgba(0, 123, 255, 0.1)',
                    tension: 0.4
                }, {
                    label: '通过率',
                    data: data.map(item => item.passRate),
                    borderColor: '#28a745',
                    backgroundColor: 'rgba(40, 167, 69, 0.1)',
                    tension: 0.4
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 100
                    }
                },
                plugins: {
                    legend: {
                        position: 'top'
                    }
                }
            }
        });
    }

    createModuleDefectChart(data) {
        const ctx = document.getElementById('moduleDefectChart').getContext('2d');

        if (this.charts.module) {
            this.charts.module.destroy();
        }

        this.charts.module = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.map(item => item.module),
                datasets: [{
                    label: '缺陷数量',
                    data: data.map(item => item.count),
                    backgroundColor: '#ffc107',
                    borderColor: '#ffb000',
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

    createEnvDefectChart(data) {
        const ctx = document.getElementById('envDefectChart').getContext('2d');

        if (this.charts.env) {
            this.charts.env.destroy();
        }

        this.charts.env = new Chart(ctx, {
            type: 'doughnut',
            data: {
                labels: data.map(item => item.environment),
                datasets: [{
                    data: data.map(item => item.count),
                    backgroundColor: data.map(item => item.color),
                    borderWidth: 2,
                    borderColor: '#fff'
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

    // 更新故事覆盖详情
    updateStoryCoverageDetails(data) {
        const container = document.getElementById('storyCoverageDetails');

        if (!data || data.length === 0) {
            container.innerHTML = '<div class="text-center text-muted"><i class="bi bi-info-circle"></i><p>暂无故事覆盖数据</p></div>';
            return;
        }

        let html = '';
        data.forEach(item => {
            const statusClass = item.covered ? 'covered' : 'not-covered';
            const statusIcon = item.covered ? 'bi-check-circle-fill text-success' : 'bi-x-circle-fill text-danger';
            const statusText = item.covered ? '已覆盖' : '未覆盖';

            html += `
                <div class="story-coverage-item ${statusClass}">
                    <div class="d-flex justify-content-between align-items-center">
                        <div>
                            <i class="bi ${statusIcon}"></i>
                            <strong>${item.story}</strong>
                        </div>
                        <div class="text-end">
                            <span class="badge bg-primary">${item.testCaseCount} 个测试用例</span>
                            <span class="ms-2">${statusText}</span>
                        </div>
                    </div>
                </div>
            `;
        });

        container.innerHTML = html;
    }

    // 更新测试周期执行详情表格
    updateCycleExecutionTable(data) {
        const tbody = document.querySelector('#cycleExecutionTable tbody');

        if (!data || data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="7" class="text-center text-muted"><i class="bi bi-info-circle"></i> 暂无测试周期数据</td></tr>';
            return;
        }

        let html = '';
        data.forEach(item => {
            html += `
                <tr>
                    <td><strong>${item.cycleName}</strong></td>
                    <td>${item.plannedCases}</td>
                    <td>${item.executedCases}</td>
                    <td><span class="text-success">${item.passedCases}</span></td>
                    <td><span class="text-danger">${item.failedCases}</span></td>
                    <td>
                        <span class="badge ${this.getExecutionRateBadgeClass(item.executionRate)}">${item.executionRate}%</span>
                    </td>
                    <td>
                        <span class="badge ${this.getPassRateBadgeClass(item.passRate)}">${item.passRate}%</span>
                    </td>
                </tr>
            `;
        });

        tbody.innerHTML = html;
    }

    getExecutionRateBadgeClass(rate) {
        if (rate >= 95) return 'bg-success';
        if (rate >= 85) return 'bg-info';
        if (rate >= 75) return 'bg-warning';
        return 'bg-danger';
    }

    getPassRateBadgeClass(rate) {
        if (rate >= 95) return 'bg-success';
        if (rate >= 90) return 'bg-info';
        if (rate >= 85) return 'bg-warning';
        return 'bg-danger';
    }

    // 更新版本对比表格
    updateVersionComparisonTable(data) {
        const tbody = document.querySelector('#versionComparisonTable tbody');

        if (!data || data.length === 0) {
            tbody.innerHTML = '<tr><td colspan="8" class="text-center text-muted"><i class="bi bi-info-circle"></i> 暂无对比数据</td></tr>';
            return;
        }

        let html = '';
        data.forEach(item => {
            html += `
                <tr>
                    <td><strong>${item.version}</strong></td>
                    <td>${item.defectDensity || 0}</td>
                    <td>${(item.testCoverage || 0).toFixed(1)}%</td>
                    <td>${(item.executionRate || 0).toFixed(1)}%</td>
                    <td>${(item.passRate || 0).toFixed(1)}%</td>
                    <td>${item.totalDefects || 0}</td>
                    <td>${item.totalTestCases || 0}</td>
                    <td><span class="badge ${this.getQualityBadgeClass(item.qualityLevel)}">${item.qualityLevel}</span></td>
                </tr>
            `;
        });

        tbody.innerHTML = html;
    }

    getQualityBadgeClass(level) {
        switch (level) {
            case '优秀': return 'bg-success';
            case '良好': return 'bg-info';
            case '一般': return 'bg-warning';
            case '需改进': return 'bg-danger';
            default: return 'bg-secondary';
        }
    }

    // 清空报表
    clearReport() {
        // 清空指标卡片
        ['defectDensity', 'testCoverage', 'executionRate', 'passRate'].forEach(id => {
            const el = document.getElementById(id);
            const levelEl = document.getElementById(id + 'Level');

            if (el) el.textContent = '-';
            if (levelEl) {
                levelEl.textContent = '-';
                levelEl.className = 'level-badge';
            }
        });

        // 销毁所有图表
        Object.values(this.charts).forEach(chart => {
            if (chart) chart.destroy();
        });
        this.charts = {};

        // 清空详情区域
        document.getElementById('storyCoverageDetails').innerHTML = '<div class="text-center text-muted"><i class="bi bi-info-circle"></i><p>请选择版本查看故事覆盖详情</p></div>';
        document.querySelector('#cycleExecutionTable tbody').innerHTML = '<tr><td colspan="7" class="text-center text-muted"><i class="bi bi-info-circle"></i> 请选择版本查看测试周期详情</td></tr>';

        // 隐藏对比区域
        document.getElementById('versionComparisonSection').style.display = 'none';
    }

    // 导出报表
    exportReport() {
        if (!this.currentProjectId || !this.currentVersion) {
            this.showError('请先选择项目和版本');
            return;
        }

        // 简单的导出功能 - 实际项目中可以生成PDF或Excel
        const reportData = {
            project: this.currentProjectId,
            version: this.currentVersion,
            exportTime: new Date().toLocaleString()
        };

        console.log('导出报表数据:', reportData);
        this.showSuccess('报表导出功能开发中...');
    }

    // 显示加载状态
    showLoading(show) {
        // 可以添加加载动画
        if (show) {
            console.log('加载中...');
        } else {
            console.log('加载完成');
        }
    }

    // 显示错误信息
    showError(message) {
        console.error('错误:', message);

        // 创建错误提示框
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-danger alert-dismissible fade show position-fixed';
        alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; max-width: 400px;';
        alertDiv.innerHTML = `
            <i class="bi bi-exclamation-triangle"></i> <strong>错误:</strong> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        document.body.appendChild(alertDiv);

        // 5秒后自动移除
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.parentNode.removeChild(alertDiv);
            }
        }, 5000);
    }

    // 显示成功信息
    showSuccess(message) {
        console.log('成功:', message);

        // 创建成功提示框
        const alertDiv = document.createElement('div');
        alertDiv.className = 'alert alert-success alert-dismissible fade show position-fixed';
        alertDiv.style.cssText = 'top: 20px; right: 20px; z-index: 9999; max-width: 400px;';
        alertDiv.innerHTML = `
            <i class="bi bi-check-circle"></i> <strong>成功:</strong> ${message}
            <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
        `;
        document.body.appendChild(alertDiv);

        // 3秒后自动移除
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.parentNode.removeChild(alertDiv);
            }
        }, 3000);
    }

    // 更新发布阶段指标
    updateReleasePhaseMetrics(data) {
        if (data && data.phaseStats) {
            const stats = data.phaseStats;
            
            // 更新发布前后缺陷数量
            document.getElementById('preReleaseDefects').textContent = stats.preReleaseDefects;
            document.getElementById('postReleaseDefects').textContent = stats.postReleaseDefects;
            
            // 更新逃逸率
            document.getElementById('escapeRate').textContent = stats.escapeRate + '%';
            document.getElementById('escapeRateLevel').textContent = stats.qualityLevel;
            document.getElementById('escapeRateLevel').className = 'level-badge ' + this.getLevelClass(stats.qualityLevel);
            
            // 更新测试有效性
            document.getElementById('testEffectiveness').textContent = stats.testEffectiveness + '%';
            document.getElementById('testEffectivenessLevel').textContent = stats.testEffectiveness >= 70 ? '良好' : '需改进';
            
            // 动态更新卡片颜色
            this.updateReleasePhaseCardColors(stats);
        }
    }

    // 更新发布阶段卡片颜色
    updateReleasePhaseCardColors(stats) {
        // 逃逸率卡片颜色
        const escapeRateCard = document.querySelector('#escapeRate').closest('.metric-card');
        escapeRateCard.className = 'metric-card ' + this.getCardLevelClass(stats.qualityLevel);
        
        // 测试有效性卡片颜色
        const effectivenessCard = document.querySelector('#testEffectiveness').closest('.metric-card');
        const effectivenessLevel = stats.testEffectiveness >= 70 ? '良好' : '需改进';
        effectivenessCard.className = 'metric-card ' + this.getCardLevelClass(effectivenessLevel);
    }

    // 更新质量洞察和改进建议
    updateQualityInsights(data) {
        if (data && data.phaseStats) {
            const stats = data.phaseStats;
            const insights = document.getElementById('qualityInsights');
            const suggestions = document.getElementById('suggestionsList');
            
            // 更新洞察信息
            insights.innerHTML = `
                <p><strong>风险提示：</strong> 发布后发现了 <span class="text-danger">${stats.postReleaseDefects}个缺陷</span>，逃逸率为 <span class="text-warning">${stats.escapeRate}%</span>，质量等级：<span class="badge ${this.getQualityBadgeClass(stats.qualityLevel)}">${stats.qualityLevel}</span></p>
                <p><strong>对比分析：</strong> 相比发布前发现的${stats.preReleaseDefects}个缺陷，测试有效性为${stats.testEffectiveness}%。</p>
            `;
            
            // 更新改进建议
            if (data.improvements && data.improvements.length > 0) {
                suggestions.innerHTML = data.improvements.map(item => `<li>${item}</li>`).join('');
            }
        }
    }

    // 更新发布阶段图表
    updateReleasePhaseCharts(data) {
        if (data) {
            // 更新发布质量趋势图
            if (data.qualityTrend) {
                this.createQualityTrendChart(data.qualityTrend);
            }
            
            // 更新时间分布图
            if (data.timeDistribution) {
                this.createTimeDistributionChart(data.timeDistribution);
            }
            
            // 更新严重程度对比图
            if (data.severityComparison) {
                this.createSeverityComparisonChart(data.severityComparison);
            }
        }
    }

    // 创建发布质量趋势图
    createQualityTrendChart(data) {
        const ctx = document.getElementById('qualityTrendChart').getContext('2d');
        
        if (this.charts.qualityTrend) {
            this.charts.qualityTrend.destroy();
        }
        
        this.charts.qualityTrend = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data.map(item => item.version),
                datasets: [{
                    label: '缺陷逃逸率',
                    data: data.map(item => item.escapeRate),
                    borderColor: '#dc3545',
                    backgroundColor: 'rgba(220, 53, 69, 0.1)',
                    tension: 0.4,
                    yAxisID: 'y'
                }, {
                    label: '总缺陷数',
                    data: data.map(item => item.totalDefects),
                    borderColor: '#007bff',
                    backgroundColor: 'rgba(0, 123, 255, 0.1)',
                    tension: 0.4,
                    yAxisID: 'y1'
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        type: 'linear',
                        display: true,
                        position: 'left',
                        title: {
                            display: true,
                            text: '逃逸率 (%)'
                        }
                    },
                    y1: {
                        type: 'linear',
                        display: true,
                        position: 'right',
                        title: {
                            display: true,
                            text: '缺陷数量'
                        },
                        grid: {
                            drawOnChartArea: false,
                        },
                    }
                }
            }
        });
    }

    // 创建时间分布图
    createTimeDistributionChart(data) {
        const ctx = document.getElementById('timeDistributionChart').getContext('2d');
        
        if (this.charts.timeDistribution) {
            this.charts.timeDistribution.destroy();
        }
        
        this.charts.timeDistribution = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.map(item => item.timeRange),
                datasets: [{
                    label: '缺陷数量',
                    data: data.map(item => item.count),
                    backgroundColor: ['#ff6b6b', '#ffa726', '#66bb6a'],
                    borderColor: ['#f44336', '#ff9800', '#4caf50'],
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

    // 创建严重程度对比图
    createSeverityComparisonChart(data) {
        const ctx = document.getElementById('severityComparisonChart').getContext('2d');
        
        if (this.charts.severityComparison) {
            this.charts.severityComparison.destroy();
        }
        
        const severityLabels = ['致命', '严重', '一般', '轻微'];
        const preReleaseData = severityLabels.map(severity => {
            const item = data.preRelease.find(d => d.severity === severity);
            return item ? item.count : 0;
        });
        const postReleaseData = severityLabels.map(severity => {
            const item = data.postRelease.find(d => d.severity === severity);
            return item ? item.count : 0;
        });
        
        this.charts.severityComparison = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: severityLabels,
                datasets: [{
                    label: '发布前发现',
                    data: preReleaseData,
                    backgroundColor: 'rgba(40, 167, 69, 0.8)',
                    borderColor: '#28a745',
                    borderWidth: 1
                }, {
                    label: '发布后发现',
                    data: postReleaseData,
                    backgroundColor: 'rgba(220, 53, 69, 0.8)',
                    borderColor: '#dc3545',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: '缺陷数量'
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: '严重程度'
                        }
                    }
                },
                plugins: {
                    legend: {
                        position: 'top'
                    },
                    title: {
                        display: true,
                        text: '发布前后缺陷严重程度对比分析'
                    }
                }
            }
        });
    }

    // 更新缺陷密度指标
    updateDefectDensity(data) {
        if (data) {
            const defectDensityEl = document.getElementById('defectDensity');
            const defectDensityLevelEl = document.getElementById('defectDensityLevel');
            const defectDensityCardEl = document.getElementById('defectDensityCard');

            // 显示主要的缺陷密度（基于独立用例）
            if (defectDensityEl) defectDensityEl.textContent = data.caseBasedDensity + '%';
            if (defectDensityLevelEl) {
                defectDensityLevelEl.textContent = data.level || '未知';
                defectDensityLevelEl.className = 'level-badge ' + this.getLevelClass(data.level);
            }
            if (defectDensityCardEl) {
                this.updateCardLevel(defectDensityCardEl, data.level);

                // 添加详细信息到卡片
                const existingDetail = defectDensityCardEl.querySelector('.density-detail');
                if (existingDetail) existingDetail.remove();

                const detailDiv = document.createElement('div');
                detailDiv.className = 'density-detail';
                detailDiv.style.cssText = 'font-size: 11px; margin-top: 8px; opacity: 0.8;';
                detailDiv.innerHTML = `
                    <div>独立用例: ${data.caseBasedDensity}%</div>
                    <div>执行次数: ${data.executionBasedDensity}%</div>
                    <div>加权密度: ${data.weightedDensity}%</div>
                `;
                defectDensityCardEl.appendChild(detailDiv);
            }

            // 更新统计信息显示
            this.updateDefectStatistics(data);
        }
    }

    // 新增: 更新缺陷统计信息
    updateDefectStatistics(data) {
        const container = document.querySelector('.chart-container');
        if (container) {
            let statsDiv = container.querySelector('.defect-statistics');
            if (!statsDiv) {
                statsDiv = document.createElement('div');
                statsDiv.className = 'defect-statistics';
                statsDiv.style.cssText = 'background: #f8f9fa; border-radius: 8px; padding: 15px; margin: 15px 0;';
                container.appendChild(statsDiv);
            }

            statsDiv.innerHTML = `
                <h6><i class="bi bi-info-circle text-info"></i> 缺陷统计详情</h6>
                <div class="row">
                    <div class="col-md-4">
                        <small class="text-muted">独立测试用例</small>
                        <div class="fw-bold">${data.uniqueTestCases}个</div>
                    </div>
                    <div class="col-md-4">
                        <small class="text-muted">总执行次数</small>
                        <div class="fw-bold">${data.totalExecutions}次</div>
                    </div>
                    <div class="col-md-4">
                        <small class="text-muted">测试周期数</small>
                        <div class="fw-bold">${data.totalCycles}个</div>
                    </div>
                </div>
                <div class="row mt-2">
                    <div class="col-md-4">
                        <small class="text-muted">独立缺陷数</small>
                        <div class="fw-bold text-danger">${data.uniqueDefects}个</div>
                    </div>
                    <div class="col-md-4">
                        <small class="text-muted">总缺陷实例</small>
                        <div class="fw-bold text-warning">${data.totalDefectInstances}个</div>
                    </div>
                    <div class="col-md-4">
                        <small class="text-muted">环境特定缺陷</small>
                        <div class="fw-bold text-info">${data.environmentSpecificDefects}个</div>
                    </div>
                </div>
            `;
        }
    }
}

// 初始化报表
const qualityReport = new VersionQualityReport();