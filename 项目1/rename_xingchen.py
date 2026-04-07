import os

EXCLUDE_DIRS = {'.git', '.idea', '.vscode', 'node_modules', 'dist', 'target', '.m2'}
EXTENSIONS = ('.java', '.xml', '.vue', '.js', '.ts', '.yml', '.yaml', '.properties', '.md', '.txt', '.sql', '.html', '.json', '.bat', '.sh', '.scss', '.css', '.xml.vm', '.java.vm', '.vue.vm', '.ts.vm')

TEXT_REPLACEMENTS = [
    ("若依", "星辰"),
    ("RuoYi", "XingChen"),
    ("ruoyi", "xingchen"),
    ("rouyi", "xingchen")
]

def replace_in_file(filepath):
    encodings = ['utf-8', 'gbk']
    content = None
    used_encoding = None
    for enc in encodings:
        try:
            with open(filepath, 'r', encoding=enc) as f:
                content = f.read()
            used_encoding = enc
            break
        except Exception:
            pass

    if content is None: return False

    new_content = content
    for old, new in TEXT_REPLACEMENTS:
        new_content = new_content.replace(old, new)

    if new_content != content:
        try:
            with open(filepath, 'w', encoding=used_encoding) as f:
                f.write(new_content)
            return True
        except Exception as e:
            print(f"Failed to write file {filepath}: {e}")
            return False
    return False

def get_new_name(name):
    new_name = name
    for old, new in TEXT_REPLACEMENTS:
        new_name = new_name.replace(old, new)
    return new_name

def process_project(root_dir):
    files_to_process = []
    dirs_to_rename = []

    for dirpath, dirnames, filenames in os.walk(root_dir):
        dirnames[:] = [d for d in dirnames if d not in EXCLUDE_DIRS]

        for d in dirnames:
            dirs_to_rename.append(os.path.join(dirpath, d))

        for f in filenames:
            if f.endswith(EXTENSIONS) or f in ('pom.xml', 'package.json', 'vite.config.js', 'vue.config.js'):
                files_to_process.append(os.path.join(dirpath, f))
            elif get_new_name(f) != f:
                files_to_process.append(os.path.join(dirpath, f))

    count_files_modified = 0
    # 1. Replace contents
    for filepath in set(files_to_process):
        if os.path.isfile(filepath):
            if replace_in_file(filepath):
                count_files_modified += 1

    count_files_renamed = 0
    # 2. Rename files
    for filepath in set(files_to_process):
        if os.path.isfile(filepath):
            basename = os.path.basename(filepath)
            new_basename = get_new_name(basename)
            if new_basename != basename:
                new_filepath = os.path.join(os.path.dirname(filepath), new_basename)
                try:
                    os.rename(filepath, new_filepath)
                    count_files_renamed += 1
                except PermissionError:
                    print(f"Warning: Could not rename file {filepath} due to PermissionError.")

    count_dirs_renamed = 0
    # 3. Rename directories (deepest first to prevent path breaking)
    dirs_to_rename.sort(key=lambda x: x.count(os.sep), reverse=True)
    for dirpath in dirs_to_rename:
        if os.path.isdir(dirpath):
            basename = os.path.basename(dirpath)
            new_basename = get_new_name(basename)
            if new_basename != basename:
                new_dirpath = os.path.join(os.path.dirname(dirpath), new_basename)
                try:
                    os.rename(dirpath, new_dirpath)
                    count_dirs_renamed += 1
                except PermissionError:
                    print(f"Warning: Could not rename directory {dirpath} due to PermissionError (it might be in use).")
                
    print(f"Modified {count_files_modified} files content.")
    print(f"Renamed {count_files_renamed} files.")
    print(f"Renamed {count_dirs_renamed} directories.")

if __name__ == "__main__":
    process_project(r"e:\项目1")
    print("Replacement complete.")
