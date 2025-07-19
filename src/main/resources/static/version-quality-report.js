// 版本质量分析报表JavaScript
class VersionQualityReport {
    constructor() {
        this.apiBase = '/api/versionQualityReport';
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
            const response = await fetch(`/api/signOff/getProjectVersion?projectId=${this.currentProjectId}`);
            const result = await response.json();
            const versionSelect = document.getElementById('versionSelect');
            const compareSelect = document.getElementById('compareVersionSelect');

            versionSelect.innerHTML = '<option value="">请选择版本</option>';
            compareSelect.innerHTML = '<option value="">选择对比版本</option>';

            if (result.data && Array.isArray(result.data)) {
                result.data.forEach(version => {
                    const option1 = document.createElement('option');
                    const option2 = document.createElement('option');
                    option1.value = version.value || version.id;
                    option1.textContent = version.label || version.name;
                    option2.value = version.value || version.id;
                    option2.textContent = version.label || version.name;
                    versionSelect.appendChild(option1);
                    compareSelect.appendChild(option2);
                });
            }
        } catch (error) {
            console.error('加载版本列表失败:', error);
            this.showError('加载版本列表失败');
        }
    }

    // 生成报表
    async loadReport() {
        if (!this.currentProjectId || !this.currentVersion) {
            this.showError('请先选择项目和版本');
            return;
        }

        this.showLoading();

        try {
            // 并行加载各种数据
            const [defectData, coverageData, distributionData, executionData] = await Promise.all([
                this.fetchDefectDensity(),
                this.fetchTestCoverage(),
                this.fetchDefectDistribution(),
                this.fetchExecutionRate()
            ]);

            // 更新指标卡片
            this.updateMetricCards(defectData, coverageData, executionData);

            // 更新图表
            this.updateCharts(distributionData, executionData);

            // 更新详情表格
            this.updateDetailTables();

        } catch (error) {
            console.error('生成报表失败:', error);
            this.showError('生成报表失败');
        } finally {
            this.hideLoading();
        }
    }

    // 获取缺陷密度数据
    async fetchDefectDensity() {
        const response = await fetch(`${this.apiBase}/getDefectDensity/${this.currentProjectId}/${this.currentVersion}`);
        return await response.json();
    }

    // 获取测试覆盖率数据
    async fetchTestCoverage() {
        const response = await fetch(`${this.apiBase}/getTestCoverage/${this.currentProjectId}/${this.currentVersion}`);
        return await response.json();
    }

    // 获取缺陷分布数据
    async fetchDefectDistribution() {
        const response = await fetch(`${this.apiBase}/getDefectDistribution/${this.currentProjectId}/${this.currentVersion}`);
        return await response.json();
    }

    // 获取执行率数据
    async fetchExecutionRate() {
        const response = await fetch(`${this.apiBase}/getExecutionRate/${this.currentProjectId}/${this.currentVersion}`);
        return await response.json();
    }

    // 更新指标卡片
    updateMetricCards(defectData, coverageData, executionData) {
        // 更新缺陷密度
        if (defectData.data) {
            document.getElementById('defectDensity').textContent = defectData.data.density || '0';
            this.updateLevelBadge('defectDensityLevel', defectData.data.level);
        }

        // 更新测试覆盖率
        if (coverageData.data) {
            document.getElementById('testCoverage').textContent = coverageData.data.coverage + '%' || '0%';
            this.updateLevelBadge('testCoverageLevel', coverageData.data.coverageLevel);
        }

        // 更新执行率
        if (executionData.data) {
            document.getElementById('executionRate').textContent = executionData.data.executionRate + '%' || '0%';
            document.getElementById('passRate').textContent = executionData.data.passRate + '%' || '0%';
            this.updateLevelBadge('executionRateLevel', executionData.data.executionLevel);
            this.updateLevelBadge('passRateLevel', executionData.data.passLevel);
        }
    }

    // 更新等级徽章
    updateLevelBadge(elementId, level) {
        const badge = document.getElementById(elementId);
        badge.textContent = level || '-';
        badge.className = 'level-badge ' + this.getLevelClass(level);
    }

    // 获取等级样式类
    getLevelClass(level) {
        switch(level) {
            case '优秀': return 'level-excellent';
            case '良好': return 'level-good';
            case '一般': return 'level-average';
            case '需改进': return 'level-poor';
            default: return 'level-average';
        }
    }

    // 更新图表
    updateCharts(distributionData, executionData) {
        // 销毁旧图表
        Object.values(this.charts).forEach(chart => {
            if (chart) chart.destroy();
        });

        // 创建缺陷严重程度分布饼图
        if (distributionData.data && distributionData.data.severityDistribution) {
            this.createDefectSeverityChart(distributionData.data.severityDistribution);
        }

        // 创建测试执行趋势图
        if (executionData.data && executionData.data.trendData) {
            this.createExecutionTrendChart(executionData.data.trendData);
        }

        // 创建模块缺陷分布图
        if (distributionData.data && distributionData.data.moduleDistribution) {
            this.createModuleDefectChart(distributionData.data.moduleDistribution);
        }

        // 创建环境缺陷对比图
        if (distributionData.data && distributionData.data.envDistribution) {
            this.createEnvDefectChart(distributionData.data.envDistribution);
        }
    }

    // 创建缺陷严重程度分布饼图
    createDefectSeverityChart(data) {
        const ctx = document.getElementById('defectSeverityChart').getContext('2d');
        this.charts.defectSeverity = new Chart(ctx, {
            type: 'pie',
            data: {
                labels: data.map(item => item.severity),
                datasets: [{
                    data: data.map(item => item.count),
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

    // 创建测试执行趋势图
    createExecutionTrendChart(data) {
        const ctx = document.getElementById('executionTrendChart').getContext('2d');
        this.charts.executionTrend = new Chart(ctx, {
            type: 'line',
            data: {
                labels: data.map(item => item.date),
                datasets: [{
                    label: '执行率',
                    data: data.map(item => item.executionRate),
                    borderColor: '#36A2EB',
                    backgroundColor: 'rgba(54, 162, 235, 0.1)',
                    fill: true
                }, {
                    label: '通过率',
                    data: data.map(item => item.passRate),
                    borderColor: '#4BC0C0',
                    backgroundColor: 'rgba(75, 192, 192, 0.1)',
                    fill: true
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

    // 创建模块缺陷分布图
    createModuleDefectChart(data) {
        const ctx = document.getElementById('moduleDefectChart').getContext('2d');
        this.charts.moduleDefect = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.map(item => item.module),
                datasets: [{
                    label: '缺陷数量',
                    data: data.map(item => item.count),
                    backgroundColor: '#FF6384'
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

    // 创建环境缺陷对比图
    createEnvDefectChart(data) {
        const ctx = document.getElementById('envDefectChart').getContext('2d');
        this.charts.envDefect = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: data.map(item => item.env),
                datasets: [{
                    label: '缺陷数量',
                    data: data.map(item => item.count),
                    backgroundColor: '#FFCE56'
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

    // 更新详情表格
    updateDetailTables() {
        // 这里可以添加更新故事覆盖详情和测试周期详情的代码
        // 根据实际API返回的数据格式来实现
    }

    // 版本对比
    async loadComparison() {
        const compareVersion = document.getElementById('compareVersionSelect').value;
        if (!this.currentProjectId || !this.currentVersion || !compareVersion) {
            this.showError('请选择项目、版本和对比版本');
            return;
        }

        try {
            const response = await fetch(`${this.apiBase}/getVersionComparison/${this.currentProjectId}?startV=${this.currentVersion}&endV=${compareVersion}`);
            const result = await response.json();

            // 显示对比表格
            document.getElementById('versionComparisonSection').style.display = 'block';
            this.updateComparisonTable(result.data);

        } catch (error) {
            console.error('版本对比失败:', error);
            this.showError('版本对比失败');
        }
    }

    // 更新对比表格
    updateComparisonTable(data) {
        const tbody = document.querySelector('#versionComparisonTable tbody');
        tbody.innerHTML = '';

        if (data && Array.isArray(data)) {
            data.forEach(version => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${version.version}</td>
                    <td>${version.defectDensity}</td>
                    <td>${version.testCoverage}%</td>
                    <td>${version.executionRate}%</td>
                    <td>${version.passRate}%</td>
                    <td>${version.totalDefects}</td>
                    <td>${version.totalCases}</td>
                    <td><span class="level-badge ${this.getLevelClass(version.qualityLevel)}">${version.qualityLevel}</span></td>
                `;
                tbody.appendChild(row);
            });
        }
    }

    // 导出报表
    exportReport() {
        if (!this.currentProjectId || !this.currentVersion) {
            this.showError('请先生成报表后再导出');
            return;
        }

        // 这里可以实现PDF导出功能
        this.showInfo('导出功能开发中...');
    }

    // 显示错误信息
    showError(message) {
        // 简单的错误提示实现
        alert('错误: ' + message);
    }

    // 显示信息
    showInfo(message) {
        alert('信息: ' + message);
    }

    // 显示加载状态
    showLoading() {
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

        // 隐藏对比表格
        document.getElementById('versionComparisonSection').style.display = 'none';

        // 清空详情区域
        document.getElementById('storyCoverageDetails').innerHTML = `
            <div class="text-center text-muted">
                <i class="bi bi-info-circle"></i>
                <p>请选择版本查看故事覆盖详情</p>
            </div>
        `;

        const tbody = document.querySelector('#cycleExecutionTable tbody');
        tbody.innerHTML = `
            <tr>
                <td colspan="7" class="text-center text-muted">
                    <i class="bi bi-info-circle"></i> 请选择版本查看测试周期详情
                </td>
            </tr>
        `;
    }
}

// 初始化报表
const qualityReport = new VersionQualityReport();