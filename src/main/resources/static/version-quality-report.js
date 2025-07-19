
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
        try {
            const response = await fetch('/api/project/list');
            const result = await response.json();
            const select = document.getElementById('projectSelect');

            select.innerHTML = '<option value="">请选择项目</option>';
            if (result.data && Array.isArray(result.data)) {
                result.data.forEach(project => {
                    const option = document.createElement('option');
                    option.value = project.id;
                    option.textContent = project.projectName || project.name;
                    select.appendChild(option);
                });
            }
        } catch (error) {
            console.error('加载项目列表失败:', error);
            this.showError('加载项目列表失败');
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
                executionRateResp
            ] = await Promise.all([
                fetch(`${this.apiBase}/getDefectDensity/${this.currentProjectId}/${this.currentVersion}`),
                fetch(`${this.apiBase}/getTestCoverage/${this.currentProjectId}/${this.currentVersion}`),
                fetch(`${this.apiBase}/getDefectDistribution/${this.currentProjectId}/${this.currentVersion}`),
                fetch(`${this.apiBase}/getExecutionRate/${this.currentProjectId}/${this.currentVersion}`)
            ]);

            const defectDensityData = await defectDensityResp.json();
            const testCoverageData = await testCoverageResp.json();
            const defectDistributionData = await defectDistributionResp.json();
            const executionRateData = await executionRateResp.json();

            // 更新指标卡片
            this.updateMetricsCards(defectDensityData.data, testCoverageData.data, executionRateData.data);

            // 更新图表
            this.updateCharts(defectDistributionData.data, executionRateData.data, defectDensityData.data);

            // 更新故事覆盖详情
            this.updateStoryCoverageDetails(testCoverageData.data.storyCoverageDetails || []);

            // 更新测试周期执行详情
            this.updateCycleExecutionTable(executionRateData.data.cycleExecutionDetails || []);

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
        this.updateMetricCard('defectDensity', defectData.defectDensity || 0, defectData.level || '-');

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

        if (valueEl) valueEl.textContent = value;
        if (levelEl) {
            levelEl.textContent = level;
            levelEl.className = 'level-badge ' + this.getLevelClass(level);
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
        alert('错误: ' + message);
    }

    // 显示成功信息
    showSuccess(message) {
        alert('成功: ' + message);
    }
}

// 初始化报表
const qualityReport = new VersionQualityReport();
