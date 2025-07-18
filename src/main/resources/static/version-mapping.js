
// 版本映射管理JavaScript
class VersionMappingManager {
    constructor() {
        this.apiBase = '/release/versionMapping';
        this.currentProjectId = null;
        this.init();
    }

    init() {
        this.loadProjects();
        this.bindEvents();
        document.getElementById('projectId').addEventListener('change', (e) => {
            this.currentProjectId = e.target.value;
            this.loadMappings();
        });
    }

    bindEvents() {
        document.getElementById('mappingForm').addEventListener('submit', (e) => {
            e.preventDefault();
            this.batchCreateMapping();
        });
    }

    // 加载项目列表
    async loadProjects() {
        try {
            const response = await fetch('/project/list'); // 假设有项目列表API
            const result = await response.json();
            const select = document.getElementById('projectId');
            
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

    // 批量创建版本映射
    async batchCreateMapping() {
        const formData = {
            projectId: parseInt(document.getElementById('projectId').value),
            releaseVersion: document.getElementById('releaseVersion').value,
            devVersions: this.parseVersions(document.getElementById('devVersions').value),
            stgVersions: this.parseVersions(document.getElementById('stgVersions').value),
            remark: document.getElementById('remark').value
        };

        // 假设有releaseId，这里简化处理
        formData.releaseId = Date.now(); // 实际应该从release表获取

        try {
            const response = await fetch(`${this.apiBase}/batchCreate`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });

            const result = await response.json();
            if (result.success) {
                alert('版本映射创建成功！');
                this.clearForm();
                this.loadMappings();
            } else {
                alert('创建失败：' + result.message);
            }
        } catch (error) {
            console.error('创建版本映射失败:', error);
            alert('创建失败，请检查网络连接');
        }
    }

    // 解析版本字符串为数组
    parseVersions(versionsText) {
        if (!versionsText.trim()) return [];
        return versionsText.split('\n')
            .map(v => v.trim())
            .filter(v => v.length > 0);
    }

    // 加载版本映射列表
    async loadMappings() {
        if (!this.currentProjectId) return;

        try {
            const response = await fetch(`${this.apiBase}/getByProject/${this.currentProjectId}`);
            const result = await response.json();
            
            if (result.success) {
                this.renderMappingTable(result.data);
            }
        } catch (error) {
            console.error('加载版本映射失败:', error);
        }
    }

    // 渲染映射表格
    renderMappingTable(mappings) {
        const tbody = document.querySelector('#mappingTable tbody');
        tbody.innerHTML = '';

        // 按发布版本分组显示
        const groupedMappings = this.groupByReleaseVersion(mappings);
        
        Object.keys(groupedMappings).forEach(releaseVersion => {
            const releaseMappings = groupedMappings[releaseVersion];
            
            releaseMappings.forEach((mapping, index) => {
                const row = document.createElement('tr');
                row.innerHTML = `
                    <td>${index === 0 ? releaseVersion : ''}</td>
                    <td><span class="badge ${this.getEnvBadgeClass(mapping.env)}">${mapping.env}</span></td>
                    <td>${mapping.envVersion}</td>
                    <td>${mapping.remark || ''}</td>
                    <td>
                        <button class="btn btn-sm btn-outline-danger" onclick="versionManager.deleteMapping(${mapping.id})">
                            删除
                        </button>
                    </td>
                `;
                tbody.appendChild(row);
            });
        });
    }

    // 按发布版本分组
    groupByReleaseVersion(mappings) {
        const grouped = {};
        mappings.forEach(mapping => {
            if (!grouped[mapping.releaseVersion]) {
                grouped[mapping.releaseVersion] = [];
            }
            grouped[mapping.releaseVersion].push(mapping);
        });
        return grouped;
    }

    // 获取环境徽章样式
    getEnvBadgeClass(env) {
        switch(env) {
            case 'dev': return 'bg-info';
            case 'stg': return 'bg-warning';
            case 'prod': return 'bg-success';
            default: return 'bg-secondary';
        }
    }

    // 删除映射
    async deleteMapping(id) {
        if (!confirm('确定要删除这个版本映射吗？')) return;

        try {
            const response = await fetch(`${this.apiBase}/delete/${id}`, {
                method: 'DELETE'
            });

            const result = await response.json();
            if (result.success) {
                alert('删除成功！');
                this.loadMappings();
            } else {
                alert('删除失败：' + result.message);
            }
        } catch (error) {
            console.error('删除版本映射失败:', error);
            alert('删除失败，请检查网络连接');
        }
    }

    // 追加单个版本映射
    async addSingleMapping() {
        const formData = {
            projectId: parseInt(document.getElementById('projectId').value),
            releaseId: parseInt(document.getElementById('addReleaseId').value),
            releaseVersion: document.getElementById('addReleaseVersion').value,
            env: document.getElementById('addEnv').value,
            envVersion: document.getElementById('addEnvVersion').value,
            remark: document.getElementById('addRemark').value || ''
        };

        if (!formData.projectId || !formData.releaseId || !formData.releaseVersion || !formData.env || !formData.envVersion) {
            alert('请填写所有必填字段！');
            return;
        }

        try {
            const response = await fetch(`${this.apiBase}/add`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(formData)
            });

            const result = await response.json();
            if (result.success) {
                alert('版本映射追加成功！');
                this.clearAddForm();
                this.loadMappings();
            } else {
                alert('追加失败：' + result.message);
            }
        } catch (error) {
            console.error('追加版本映射失败:', error);
            alert('追加失败，请检查网络连接');
        }
    }

    // 清空追加表单
    clearAddForm() {
        document.getElementById('addReleaseId').value = '';
        document.getElementById('addReleaseVersion').value = '';
        document.getElementById('addEnv').value = '';
        document.getElementById('addEnvVersion').value = '';
        document.getElementById('addRemark').value = '';
    }

    // 清空表单
    clearForm() {
        document.getElementById('releaseVersion').value = '';
        document.getElementById('devVersions').value = '';
        document.getElementById('stgVersions').value = '';
        document.getElementById('remark').value = '';
    }
}

// 初始化版本映射管理器
const versionManager = new VersionMappingManager();
