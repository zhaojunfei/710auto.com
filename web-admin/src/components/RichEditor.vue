<template>
  <div style="border: 1px solid #dcdfe6; border-radius: 4px; width: 100%">
    <Toolbar :editor="editorRef" :defaultConfig="toolbarConfig" mode="default" style="border-bottom: 1px solid #dcdfe6" />
    <Editor
      :model-value="modelValue"
      :defaultConfig="editorConfig"
      mode="default"
      style="height: 320px; overflow-y: hidden"
      @onCreated="handleCreated"
      @onChange="handleChange"
    />
  </div>
</template>

<script setup lang="ts">
import '@wangeditor/editor/dist/css/style.css'
import { onBeforeUnmount, shallowRef } from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'

const props = defineProps<{ modelValue?: string }>()
const emit = defineEmits(['update:modelValue'])

const editorRef = shallowRef()

const toolbarConfig = {
  toolbarKeys: [
    'headerSelect', 'bold', 'italic', 'underline', 'through', 'color', 'bgColor',
    '|', 'justifyLeft', 'justifyCenter', 'justifyRight',
    '|', 'bulletedList', 'numberedList', 'blockquote', 'divider',
    '|', 'uploadImage', 'insertLink', 'insertTable', 'insertVideo', 'codeBlock',
    '|', 'undo', 'redo', 'fullScreen'
  ]
}

const editorConfig = {
  placeholder: '请输入内容…（支持图片粘贴上传、表格、视频、源码）',
  MENU_CONF: {
    uploadImage: {
      server: '/api/admin/file/upload?dir=richtext&bizType=richtext',
      fieldName: 'file',
      headers: { Authorization: 'Bearer ' + (localStorage.getItem('auto710_token') || '') },
      maxFileSize: 5 * 1024 * 1024,
      customInsert(res: any, insertFn: any) {
        if (res?.code === 200 && res.data?.url) {
          insertFn(res.data.url, res.data.originalName, res.data.url)
        }
      }
    },
    uploadVideo: {
      server: '/api/admin/file/upload?dir=videos&bizType=richtext',
      fieldName: 'file',
      headers: { Authorization: 'Bearer ' + (localStorage.getItem('auto710_token') || '') },
      customInsert(res: any, insertFn: any) {
        if (res?.code === 200 && res.data?.url) insertFn(res.data.url, res.data.url)
      }
    }
  }
}

function handleCreated(editor: any) {
  editorRef.value = editor
}
function handleChange(editor: any) {
  emit('update:modelValue', editor.getHtml())
}
onBeforeUnmount(() => {
  editorRef.value?.destroy()
})
</script>
